<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Revenue Report</title>
  <link rel="shortcut icon" href="<%=request.getContextPath()%>/favicon.svg" type="image/svg+xml">
  <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">
</head>
<body id="top">
<main>
  <section class="auth-section">
    <div class="container">
      <h2 class="h2 section-title">Revenue Report</h2>
      <form id="reportForm" class="auth-form">
        <input type="date" name="from" required />
        <input type="date" name="to" required />
        <button type="submit" class="btn btn-primary">View</button>
      </form>
      <p id="result"></p>
    </div>
  </section>
</main>
<script>
const base = '<%=request.getContextPath()%>';
const token = localStorage.getItem('adminToken');
document.getElementById('reportForm').addEventListener('submit',async e=>{
  e.preventDefault();
  const from=e.target.from.value;
  const to=e.target.to.value;
  const res=await fetch(base+'/api/admin/report?from='+from+'&to='+to,{headers:{Authorization:'Bearer '+token}});
  if(res.ok){
    const d=await res.json();
    document.getElementById('result').textContent='Total Point: '+d.totalPoint;
  }
});
</script>
</body>
</html>