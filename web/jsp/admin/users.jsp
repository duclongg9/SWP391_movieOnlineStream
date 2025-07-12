<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>User Management</title>
  <link rel="shortcut icon" href="<%=request.getContextPath()%>/favicon.svg" type="image/svg+xml">
  <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">
</head>
<body id="top">
<main>
  <section class="auth-section">
    <div class="container">
      <h2 class="h2 section-title">Users</h2>
      <table id="userTable" class="table">
        <thead>
          <tr><th>ID</th><th>Username</th><th>Full name</th><th>Phone</th><th>Email</th><th>Points</th><th>Status</th><th>Deleted</th><th>Action</th></tr>
        </thead>
        <tbody></tbody>
      </table>
    </div>
  </section>
</main>
<script>
const base = '<%=request.getContextPath()%>';
const token = localStorage.getItem('adminToken');
function load(){
  fetch(base + '/api/admin/users',{headers:{Authorization:'Bearer '+token}})
    .then(r=>r.json()).then(list=>{
      const tb=document.querySelector('#userTable tbody');
      tb.innerHTML='';
      list.forEach(u=>{
        const tr=document.createElement('tr');
        tr.innerHTML=`<td>${u.id}</td><td>${u.username||''}</td><td>${u.fullName||''}</td><td>${u.phone||''}</td><td>${u.email}</td><td>${u.pointBalance}</td>`+
          `<td>${u.locked? 'Locked':'Active'}</td>`+
          `<td>${u.deleted? 'Yes':'No'}</td>`+
          `<td><button data-id="${u.id}" data-action="${u.locked?'unlock':'lock'}">${u.locked?'Unlock':'Lock'}</button>`+
          `<button data-id="${u.id}" data-delete="true">Delete</button></td>`;
        tb.appendChild(tr);
      });
    });
}
document.addEventListener('click',e=>{
  if(e.target.dataset){
    if(e.target.dataset.action){
      fetch(base + '/api/admin/user/'+e.target.dataset.id+'/'+e.target.dataset.action,{method:'PATCH',headers:{Authorization:'Bearer '+token}}).then(load);
    } else if(e.target.dataset.delete){
      fetch(base + '/api/admin/user/'+e.target.dataset.id,{method:'DELETE',headers:{Authorization:'Bearer '+token}}).then(load);
    }
  }
});
load();
</script>
</body>
</html>