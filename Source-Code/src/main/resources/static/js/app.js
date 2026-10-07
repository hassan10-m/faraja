/* Faraja browser client: fetch -> REST controller -> service -> repository -> MySQL. */
const page = document.body.dataset.page;
const content = document.querySelector('#content');
const message = document.querySelector('#message');
let csrfToken = '', role = '', lookups = {};
const escapeHtml = value => String(value ?? '').replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const can = (...roles) => role === 'ADMIN' || roles.includes(role);
const display = value => value && typeof value === 'object' ? value.fullName || value.name || value.number || value.id : (value ?? '—');

async function api(path, method = 'GET', data) {
  const headers = {};
  if (data !== undefined) headers['Content-Type'] = 'application/json';
  if (method !== 'GET') headers['X-XSRF-TOKEN'] = csrfToken;
  const response = await fetch('/api' + path, {method, headers, body:data === undefined ? undefined : JSON.stringify(data)});
  if (response.redirected && response.url.includes('login.html')) { location.href='/login.html'; throw Error('Please sign in again'); }
  if (!response.ok) {
    const error = await response.json().catch(() => ({}));
    throw Error(error.error || (response.status === 403 ? 'Your account does not have permission for this action.' : 'Request failed (' + response.status + ')'));
  }
  return response.json();
}
function notify(text, error = false) {message.textContent=text;message.className=error?'error':'';}
function field(name, label, type='text', required=true, choices=null) {
  return {name,label,type,required,choices};
}
const patientField = () => field('patientId','Patient','select',true,lookups.patients);
const visitField = () => field('visitId','Visit','select',true,lookups.visits);
function fieldHtml(f) {
  const required = f.required ? 'required' : '';
  let input;
  if (f.type==='select') input=`<select name="${f.name}" ${required}><option value="">Choose…</option>${(f.choices||[]).map(o=>`<option value="${escapeHtml(o.id)}">${escapeHtml(o.label)}</option>`).join('')}</select>`;
  else if(f.type==='textarea') input=`<textarea name="${f.name}" ${required}></textarea>`;
  else input=`<input name="${f.name}" type="${f.type}" ${f.type==='number'?'step="any"':''} ${required} ${f.type==='password'?'autocomplete="new-password"':''}>`;
  return `<label>${escapeHtml(f.label)}${input}</label>`;
}
function form(title,path,fields,method='POST') {
  return `<section class="card"><h2>${escapeHtml(title)}</h2><form class="fields" data-path="${path}" data-method="${method}">${fields.map(fieldHtml).join('')}<button type="submit">Save →</button></form></section>`;
}
function table(title,rows,keys,actions=[]) {
  return `<section class="card"><h2>${escapeHtml(title)} <small>(${rows.length})</small></h2><table><thead><tr>${keys.map(k=>`<th>${escapeHtml(k)}</th>`).join('')}${actions.length?'<th>Actions</th>':''}</tr></thead><tbody>${rows.map(row=>`<tr>${keys.map(k=>`<td>${escapeHtml(display(row[k]))}</td>`).join('')}${actions.length?`<td>${actions.filter(a=>!a.when||a.when(row)).map(a=>`<button type="button" data-action="${a.name}" data-id="${row.id}">${escapeHtml(a.name)}</button>`).join(' ')}</td>`:''}</tr>`).join('')||`<tr><td colspan="${keys.length+1}">No records yet.</td></tr>`}</tbody></table></section>`;
}
function stats(data) {return `<div class="stats">${Object.entries(data).map(([k,v])=>`<div class="card stat"><strong>${escapeHtml(v)}</strong><span>${escapeHtml(k)}</span></div>`).join('')}</div>`;}
async function loadLookups() {
  const patients=await api('/patients');
  lookups.patients=patients.map(p=>({id:p.id,label:`${p.number} · ${p.fullName} (ID ${p.id})`}));
  if(['queue','consultation','laboratory','pharmacy','billing'].includes(page)) {
    const visits=await api('/visits');
    lookups.visits=visits.filter(v=>v.status!=='CLOSED').map(v=>({id:v.id,label:`Visit ${v.id} · ${v.patient.fullName} · ${v.status}`}));
  }
}
async function render() {
  let html='';
  if(['patient-register','appointments','queue','consultation','laboratory','pharmacy','billing'].includes(page))await loadLookups();
  switch(page) {
    case 'dashboard':
      html=stats(await api('/dashboard'));
      if(can('RECEPTIONIST','DOCTOR','NURSE'))html+=table('Waiting patients',await api('/queue'),['id','patient','createdAt','status']);
      break;
    case 'patients':
      html='<section class="card"><label>Search patients<input id="patient-search" placeholder="Name, patient number or phone"></label></section>';
      html+=table('Patient directory',await api('/patients'),['id','number','fullName','birthDate','sex','phone'],can('DOCTOR','NURSE')?[{name:'History'}]:[]);
      break;
    case 'patient-register':
      if(can('RECEPTIONIST'))html=form('Register patient','/patients',[field('fullName','Full name'),field('birthDate','Date of birth','date',false),field('sex','Sex','select',false,[{id:'Female',label:'Female'},{id:'Male',label:'Male'},{id:'Other',label:'Other'}]),field('phone','Phone','tel',false),field('address','Address','text',false),field('allergies','Known allergies','textarea',false)]);
      html+=table('Registered patients',await api('/patients'),['id','number','fullName','phone']);break;
    case 'appointments':
      if(can('RECEPTIONIST'))html=form('Book appointment','/appointments',[patientField(),field('scheduledAt','Date and time','datetime-local'),field('reason','Reason','text',false)]);
      html+=table('Appointments',await api('/appointments'),['id','patient','scheduledAt','reason','status'],can('RECEPTIONIST')?[{name:'Cancel',when:r=>r.status==='BOOKED'}]:[]);break;
    case 'queue': {
      if(can('RECEPTIONIST','NURSE')) {
        const appointments=await api('/appointments');
        html=form('Start a visit','/visits',[patientField(),field('appointmentId','Appointment (optional)','select',false,appointments.filter(a=>a.status==='BOOKED').map(a=>({id:a.id,label:`${a.id} · ${a.patient.fullName} · ${a.scheduledAt}`})))]);
      }
      html+=table('Waiting patients',await api('/queue'),['id','patient','createdAt','status']);break;
    }
    case 'consultation':
      if(can('NURSE','DOCTOR'))html=form('Record vital signs','/visits/{visitId}/vitals',[visitField(),field('systolic','Systolic mmHg','number',false),field('diastolic','Diastolic mmHg','number',false),field('pulse','Pulse / min','number',false),field('temperature','Temperature °C','number',false),field('weightKg','Weight kg','number',false)],'PUT');
      if(can('DOCTOR'))html+=form('Clinical consultation','/visits/{visitId}/consultation',[visitField(),field('complaint','Chief complaint','textarea'),field('history','Clinical history','textarea',false),field('examination','Examination','textarea',false),field('diagnosis','Diagnosis','textarea'),field('treatment','Treatment plan','textarea',false)],'PUT');
      html+=table('Visits',await api('/visits'),['id','patient','status','diagnosis','systolic','diastolic','temperature','weightKg'],can('DOCTOR')?[{name:'Close',when:r=>r.status==='CONSULTED'}]:[]);break;
    case 'laboratory':
      if(can('DOCTOR'))html=form('Order laboratory test','/labs',[visitField(),field('testName','Test name')]);
      html+=table('Laboratory worklist',await api('/labs'),['id','visit','testName','resultText','status'],can('LAB')?[{name:'Result',when:r=>r.status!=='COMPLETE'}]:[]);break;
    case 'pharmacy': {
      const medicines=await api('/medicines');
      const medicineField=()=>field('medicineId','Medicine','select',true,medicines.map(m=>({id:m.id,label:`${m.name} · stock ${m.stock}`})));
      if(can('PHARMACIST'))html=form('Add medicine','/medicines',[field('name','Medicine name'),field('unit','Unit'),field('stock','Opening stock','number'),field('reorderLevel','Reorder level','number'),field('unitPrice','Unit price (TZS)','number'),field('expiryDate','Expiry date','date',false)])+form('Receive additional stock','/medicines/{medicineId}/stock',[medicineField(),field('quantity','Quantity received','number'),field('reference','Delivery reference')]);
      if(can('DOCTOR'))html+=form('Prescribe medicine','/prescriptions',[visitField(),medicineField(),field('quantity','Quantity','number'),field('instructions','Dose and instructions','textarea')]);
      html+=table('Medicine inventory',medicines,['id','name','unit','stock','reorderLevel','unitPrice','expiryDate']);
      html+=table('Prescriptions',await api('/prescriptions'),['id','visit','medicine','quantity','instructions','dispensed'],can('PHARMACIST')?[{name:'Dispense',when:r=>!r.dispensed}]:[]);break;
    }
    case 'billing': {
      const invoices=await api('/invoices'),payments=await api('/payments');
      const rows=invoices.map(i=>({...i,paid:payments.filter(p=>p.invoice.id===i.id).reduce((sum,p)=>sum+Number(p.amount),0)})).map(i=>({...i,balance:(Number(i.amount)-i.paid).toFixed(2)}));
      if(can('CASHIER'))html=form('Create invoice','/invoices',[visitField(),field('amount','Total amount (TZS)','number'),field('description','Services and charges','textarea')])+form('Record payment','/payments',[field('invoiceId','Invoice','select',true,rows.filter(i=>Number(i.balance)>0).map(i=>({id:i.id,label:`Invoice ${i.id} · ${i.visit.patient.fullName} · balance ${i.balance}`}))),field('amount','Amount (TZS)','number'),field('method','Payment method','select',true,['CASH','MOBILE_MONEY','BANK'].map(x=>({id:x,label:x}))),field('reference','Payment reference','text',false)]);
      html+=table('Invoices',rows,['id','visit','amount','paid','balance','description']);html+=table('Payments',payments,['id','invoice','amount','method','reference','createdAt'],[{name:'Receipt'}]);break;
    }
    case 'reports':html=stats(await api('/reports/summary'))+'<section class="card"><button id="print-report">Print report / save PDF</button></section>';break;
    case 'settings':
      html=form('Change your password','/account/password',[field('currentPassword','Current password','password'),field('newPassword','New password (12+ characters)','password')]);
      if(can())html+=form('Create staff account','/admin/users',[field('username','Username'),field('password','Password (12+ characters)','password'),field('fullName','Full name'),field('role','Role','select',true,['ADMIN','RECEPTIONIST','DOCTOR','NURSE','LAB','PHARMACIST','CASHIER'].map(x=>({id:x,label:x})))])+table('Staff accounts',await api('/admin/users'),['id','username','fullName','role','active'])+table('Audit log',await api('/admin/audit'),['createdAt','actor','action','target']);break;
  }
  content.innerHTML=html;
  bind();
}
function bind() {
  document.querySelectorAll('form.fields').forEach(formEl=>formEl.addEventListener('submit',async event=>{
    event.preventDefault();const button=formEl.querySelector('button');button.disabled=true;
    const data=Object.fromEntries(new FormData(formEl));
    for(const input of formEl.querySelectorAll('input,select,textarea')) {
      if(data[input.name]==='') data[input.name]=null;
      else if(input.type==='number'||input.name.endsWith('Id'))data[input.name]=Number(data[input.name]);
    }
    const path=formEl.dataset.path.replace(/\{(\w+)\}/g,(_,key)=>encodeURIComponent(data[key]));
    try {await api(path,formEl.dataset.method,data);await render();notify('Saved successfully.');}
    catch(error){notify(error.message,true);button.disabled=false;}
  }));
  document.querySelectorAll('[data-action]').forEach(button=>button.addEventListener('click',async()=>{
    const id=button.dataset.id,action=button.dataset.action;
    try {
      if(action==='History') {
        const visits=await api('/patients/'+id+'/history');
        const panel=document.createElement('section');panel.className='card';
        panel.innerHTML='<h2>Patient history</h2>'+visits.map(v=>`<article><h3>Visit ${escapeHtml(v.id)} · ${escapeHtml(v.createdAt)}</h3>${['complaint','history','examination','diagnosis','treatment','systolic','diastolic','pulse','temperature','weightKg'].map(k=>`<p><b>${escapeHtml(k)}:</b> ${escapeHtml(v[k]??'—')}</p>`).join('')}</article>`).join('');
        if(!visits.length)panel.innerHTML+='<p>No visits recorded.</p>';content.prepend(panel);panel.scrollIntoView();return;
      }
      if(action==='Receipt') {
        const p=(await api('/payments')).find(p=>String(p.id)===id);
        const panel=document.createElement('section');panel.className='card';panel.innerHTML=`<h2>Faraja Dispensary · Receipt ${escapeHtml(p.id)}</h2><p>Patient: ${escapeHtml(p.invoice.visit.patient.fullName)}</p><p>Invoice: ${escapeHtml(p.invoice.id)} · Paid: TZS ${escapeHtml(p.amount)}</p><p>Method: ${escapeHtml(p.method)} · Reference: ${escapeHtml(p.reference)}</p><p>Date: ${escapeHtml(p.createdAt)}</p><button id="print-receipt">Print receipt</button>`;content.prepend(panel);panel.querySelector('button').onclick=()=>window.print();return;
      }
      if(action==='Result'){const resultText=prompt('Enter test result');if(!resultText)return;await api('/labs/'+id+'/result','PUT',{resultText});}
      else {if(!confirm(action+' this record?'))return;const routes={Dispense:'/prescriptions/'+id+'/dispense',Cancel:'/appointments/'+id+'/cancel',Close:'/visits/'+id+'/close'};await api(routes[action],'PUT');}
      await render();notify('Record updated.');
    }catch(error){notify(error.message,true);}
  }));
  const search=document.querySelector('#patient-search');if(search)search.oninput=()=>document.querySelectorAll('tbody tr').forEach(row=>row.hidden=!row.textContent.toLowerCase().includes(search.value.toLowerCase()));
  const print=document.querySelector('#print-report');if(print)print.onclick=()=>window.print();
}
(async()=>{
  try {
    const me=await api('/me');role=me.roles[0].replace('ROLE_','');csrfToken=(await api('/csrf')).token;
    document.querySelector('#account').textContent=me.username+' · '+role;
    const pages={dashboard:['ALL'],patients:['ALL'],'patient-register':['RECEPTIONIST'],appointments:['RECEPTIONIST','DOCTOR','NURSE'],queue:['RECEPTIONIST','DOCTOR','NURSE'],consultation:['DOCTOR','NURSE'],laboratory:['DOCTOR','NURSE','LAB'],pharmacy:['DOCTOR','PHARMACIST'],billing:['CASHIER'],reports:['CASHIER'],settings:['ALL']};
    document.querySelectorAll('nav a').forEach(link=>{const key=link.getAttribute('href').split('/').pop().replace('.html','');link.hidden=!(pages[key]?.includes('ALL')||can(...(pages[key]||[])));if(key===page)link.classList.add('active');});
    document.querySelector('#logout').addEventListener('submit',()=>{let input=document.createElement('input');input.type='hidden';input.name='_csrf';input.value=csrfToken;document.querySelector('#logout').append(input);});
    if(!pages[page]?.includes('ALL')&&!can(...(pages[page]||[])))throw Error('This page is not available for your role.');
    await render();
  }catch(error){notify(error.message,true);}
})();
