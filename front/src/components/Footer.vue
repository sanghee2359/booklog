<template>
  <div class="footer">
    <nav>
      <a href="https://cocoder.tistory.com" target="_blank">Blog</a> |
      <a href="https://github.com/sanghee2359/booklog.git" target="_blank">Github</a>
    </nav>
    <p>
      <span>저자 : sanghee</span><br />
      <span>이메일 : jsanghui92@gmail.com</span><br />
      <span>Copyright 2024. sanghee. All Rights Reserved.</span>
    </p>
  </div>
</template>

<style scoped lang="scss">
.footer {
  width: 100%;
  height: 110px;
  background-color: #f8f9fa; /* footer 배경색 설정 */
  text-align: center;
  padding: 20px 0;
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin-top: auto; /* 페이지 내용에 따라 하단에 위치 */
  font-size: 14px;
}

.footer nav {
  margin-bottom: 10px;
}

.footer a {
  color: #007bff;
  text-decoration: none;
}

.footer a:hover {
  text-decoration: underline;
}

.footer p {
  margin: 0;
  line-height: 1.5;
}
</style>
