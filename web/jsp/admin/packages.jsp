<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Package Management</title>
  <link rel="shortcut icon" href="<%=request.getContextPath()%>/favicon.svg" type="image/svg+xml">
  <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">
</head>
<body id="top">
     <jsp:include page="/jsp/template/header.jsp" />
<main>
  <section class="auth-section">
    <div class="container">
      <h2 class="h2 section-title">Packages</h2>
      <form id="pkgForm" class="auth-form">
        <input type="text" name="name" placeholder="Name" required />
        <input type="text" name="description" placeholder="Description" required />
        <input type="number" name="duration" placeholder="Duration days" required />
        <input type="number" name="price" placeholder="Price point" required />
        <button type="submit" class="btn btn-primary">Add</button>
      </form>
      <table id="pkgTable" class="table">
        <thead>
          <tr><th>ID</th><th>Name</th><th>Description</th><th>Duration</th><th>Price</th><th>Action</th></tr>
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
  fetch(base + '/api/admin/packages?page='+page+'&size='+pageSize,{headers:{Authorization:'Bearer '+token}})
    .then(r=>r.json()).then(res=>{
      const list=res.packages;
      const total=res.total;
      const tb=document.querySelector('#pkgTable tbody');
      tb.innerHTML='';
      list.forEach(p=>{
        const tr=document.createElement('tr');
        tr.innerHTML=`<td>${p.id}</td><td>${p.name}</td><td>${p.description}</td>`+
          `<td>${p.durationDays}</td><td>${p.pricePoint}</td>`+
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
document.getElementById('pkgForm').addEventListener('submit',async e=>{
  e.preventDefault();
  const data=new URLSearchParams(new FormData(e.target));
  await fetch(base+'/api/admin/packages',{method:'POST',headers:{Authorization:'Bearer '+token},body:data});
  e.target.reset();
  load(currentPage);
});
document.addEventListener('click',async e=>{
  if(e.target.dataset.delete){
    await fetch(base+'/api/admin/package/'+e.target.dataset.id,{method:'DELETE',headers:{Authorization:'Bearer '+token}});
    load(currentPage);
  }
});
load();
</script>
</body>
</html>