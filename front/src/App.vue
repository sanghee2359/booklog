<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <header>
      <nav>
        <RouterLink to="/">Home</RouterLink>
        <RouterLink to="/write">글 작성</RouterLink>
      </nav>
    <RouterView/>
  </header>
  <div><p>안녕하세요</p></div>

</template>

