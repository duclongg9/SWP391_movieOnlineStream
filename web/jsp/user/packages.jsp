<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Packages</title>
  <link rel="shortcut icon" href="<%=request.getContextPath()%>/favicon.svg" type="image/svg+xml">
  <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">
</head>
<body id="top">
<main>
  <section class="auth-section">
    <div class="container">
      <h2 class="h2 section-title">Packages</h2>
      <div id="pkgList"></div>
    </div>
  </section>
</main>
<script>
const base = '<%=request.getContextPath()%>';
const token = localStorage.getItem('token');
function load(){
  fetch(base + '/api/packages').then(r=>r.json()).then(list=>{
    const div=document.getElementById('pkgList');
    div.innerHTML='';
    list.forEach(p=>{
      const el=document.createElement('div');
      el.className='package-card';
      el.innerHTML=`<h3>${p.name}</h3>
        <p>${p.description||''}</p>
        <p>Duration: ${p.durationDays} days</p>
        <p>Price: ${p.pricePoint} points</p>
        <button data-id="${p.id}">Buy</button>`;
      div.appendChild(el);
    });
  });
}
document.addEventListener('click',async e=>{
  if(e.target.dataset.id){
    const res=await fetch(base+'/api/purchase/package/'+e.target.dataset.id,{method:'POST',headers:{Authorization:'Bearer '+token}});
    if(res.ok) alert('Purchased');
    else alert('Failed');
  }
});
load();
</script>
</body>
</html>