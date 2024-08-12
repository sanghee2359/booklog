<script setup lang="ts"></script>

<template>
  <div class="header">
    <img src="/images/logo_1.png" alt="logo" class="logo" />
    <div class="title">Book Log</div>
  </div>
</template>

<style scoped lang="scss">
.header {
  height: 60px;
  margin: 1rem 2rem;
  display: flex;
  justify-content: center;
  align-items: center;
}

.logo {
  width: 54px;
  object-fit: cover;
}

.title {
  font-size: 2rem;
  font-weight: 300;
  margin-left: 5px;
}
</style>
