<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Movie Management</title>
  <link rel="shortcut icon" href="<%=request.getContextPath()%>/favicon.svg" type="image/svg+xml">
  <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">
</head>
<body id="top">
<main>
  <section class="auth-section">
    <div class="container">
      <h2 class="h2 section-title">Movies</h2>
      <form id="movieForm" class="auth-form">
        <input type="text" name="title" placeholder="Title" required />
        <input type="text" name="genre" placeholder="Genre" required />
        <input type="text" name="actor" placeholder="Actor" required />
        <input type="text" name="videoPath" placeholder="Video Path" required />
        <input type="text" name="description" placeholder="Description" required />
        <input type="number" name="price" placeholder="Price point" required />
        <button type="submit" class="btn btn-primary">Add</button>
      </form>
      <table id="movieTable" class="table">
        <thead>
          <tr><th>ID</th><th>Title</th><th>Genre</th><th>Actor</th><th>Price</th><th>Action</th></tr>
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
  fetch(base + '/api/admin/movies',{headers:{Authorization:'Bearer '+token}})
    .then(r=>r.json()).then(list=>{
      const tb=document.querySelector('#movieTable tbody');
      tb.innerHTML='';
      list.forEach(m=>{
        const tr=document.createElement('tr');
        tr.innerHTML=`<td>${m.id}</td><td>${m.title}</td><td>${m.genre}</td>`+
          `<td>${m.actor}</td><td>${m.pricePoint}</td>`+
          `<td><button data-id="${m.id}" data-delete="true">Delete</button></td>`;
        tb.appendChild(tr);
      });
    });
}
document.getElementById('movieForm').addEventListener('submit',async e=>{
  e.preventDefault();
  const data=new URLSearchParams(new FormData(e.target));
  await fetch(base+'/api/admin/movies',{method:'POST',headers:{Authorization:'Bearer '+token},body:data});
  e.target.reset();
  load();
});
document.addEventListener('click',async e=>{
  if(e.target.dataset.delete){
    await fetch(base+'/api/admin/movie/'+e.target.dataset.id,{method:'DELETE',headers:{Authorization:'Bearer '+token}});
    load();
  }
});
load();
</script>
</body>
</html>