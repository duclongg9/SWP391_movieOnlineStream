<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Promotion Management</title>
  <link rel="shortcut icon" href="<%=request.getContextPath()%>/favicon.svg" type="image/svg+xml">
  <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">
</head>
<body id="top">
     <jsp:include page="/jsp/template/header.jsp" />
<main>
  <section class="auth-section">
    <div class="container">
      <h2 class="h2 section-title">Promotions</h2>
      <form id="promoForm" class="auth-form">
        <input type="text" name="code" placeholder="Code" required />
        <input type="number" step="0.01" name="discountPct" placeholder="Discount %" required />
        <input type="text" name="applyTo" placeholder="Apply to" required />
        <input type="text" name="targetType" placeholder="Target type" />
        <input type="number" name="targetId" placeholder="Target id" />
        <input type="text" name="validUntil" placeholder="YYYY-MM-DDTHH:MM:SSZ" />
        <button type="submit" class="btn btn-primary">Add</button>
      </form>
      <table id="promoTable" class="table">
        <thead>
          <tr><th>ID</th><th>Code</th><th>Discount</th><th>Apply To</th><th>Target</th><th>Valid Until</th><th>Action</th></tr>
        </thead>
        <tbody></tbody>
      </table>
      <div id="pagination" class="pagination"></div>
    </div>
  </section>
</main>
<script>
const base = '<%=request.getContextPath()%>';
const token = localStorage.getItem('adminToken');
let currentPage = 1;
const pageSize = 10;
function load(page=currentPage){
  currentPage = page;
  fetch(base+'/api/admin/promotions?page='+page+'&size='+pageSize,{headers:{Authorization:'Bearer '+token}})
    .then(r=>r.json()).then(res=>{
      const list=res.promotions;
      const total=res.total;
      const tb=document.querySelector('#promoTable tbody');
      tb.innerHTML='';
      list.forEach(p=>{
        const tr=document.createElement('tr');
        tr.innerHTML=`<td>${p.id}</td><td>${p.code}</td><td>${p.discountPct}</td>`+
          `<td>${p.applyTo}</td><td>${p.targetType||''} ${p.targetId||''}</td>`+
          `<td>${p.validUntil||''}</td>`+
          `<td><button data-id="${p.id}" data-delete="true">Delete</button></td>`;
        tb.appendChild(tr);
      });
      renderPagination(total);
    });
}

function renderPagination(total){
  const totalPages = Math.ceil(total/pageSize);
  const pag=document.getElementById('pagination');
  pag.innerHTML='';
  for(let i=1;i<=totalPages;i++){
    const btn=document.createElement('button');
    btn.textContent=i;
    if(i===currentPage) btn.disabled=true;
    btn.addEventListener('click',()=>load(i));
    pag.appendChild(btn);
  }
}
document.getElementById('promoForm').addEventListener('submit',async e=>{
  e.preventDefault();
  const data=new URLSearchParams(new FormData(e.target));
  await fetch(base+'/api/admin/promotions',{method:'POST',headers:{Authorization:'Bearer '+token'},body:data});
  e.target.reset();
  load(currentPage);
});
document.addEventListener('click',async e=>{
  if(e.target.dataset.delete){
    await fetch(base+'/api/admin/promotion/'+e.target.dataset.id,{method:'DELETE',headers:{Authorization:'Bearer '+token'}});
    load(currentPage);
  }
});
load();
</script>
</body>
</html>