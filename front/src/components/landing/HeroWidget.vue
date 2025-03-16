<template>
  <div
      id="hero"
      class="flex flex-col pt-6 px-6 lg:px-20 overflow-hidden"
      style="
      min-height: 120vh;
      background: linear-gradient(0deg, rgba(255, 255, 255, 0.2), rgba(255, 255, 255, 0.2)),
        radial-gradient(
          77.36% 256.97% at 77.36% 57.52%,
          rgb(238, 239, 175) 0%,
          rgb(195, 227, 250) 100%
        );
      clip-path: ellipse(150% 87% at 93% 13%);
    "
  >
    <div class="mx-6 md:mx-20 mt-0 md:mt-6">
      <h1 class="text-6xl font-bold text-gray-900 leading-tight">
        <span class="font-light block">함께하는 독서</span>Book Log
      </h1>

      <Button
          as="router-link"
          class="!text-xl mt-8 !px-4"
          label="Get Started"
          rounded
          to="/"
      ></Button>

      <p class="font-normal text-2xl leading-normal md:mt-4 text-gray-700">
        📚 독서 목표를 세우고 기록을 남겨보세요!<br />
        ✔ 읽을 책을 정리하고, 나만의 독서 목표를 설정하세요.<br />
        ✔ 완독한 책을 기록으로 남기고, 올해의 의미 있는 책을 선정해보세요.<br />
        ✔ 감상을 나누며 다양한 시각에서 책을 바라보는 즐거움을 경험하세요.
      </p>

    </div>
  </div>
</template>

<script lang="ts" setup></script>
