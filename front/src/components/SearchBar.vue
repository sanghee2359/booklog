<template>
  <div class="search-bar">
    <input type="text" v-model="query" placeholder="Search books..." @input="onInput" />
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'SearchBar',
  props: {
    modelValue: {
      type: String,
      required: true
    }
  },
  emits: ['update:modelValue'],
  computed: {
    query: {
      get(): string {
        return this.modelValue
      },
      set(value: string) {
        this.$emit('update:modelValue', value)
      }
    }
  },
  methods: {
    onInput() {
      this.$emit('update:modelValue', this.query)
    }
  }
})
</script>

<style scoped>
.search-bar {
  margin-bottom: 1rem;
}

input {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}
</style>
