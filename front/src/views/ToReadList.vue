<template>
  <Head :totalIncome="state.totalIncome" />
  <Form :state="state" @add-income="AddIncome" />
  <IncomeList :state="state" @remove-item="removeItem" />
</template>

<script>
import { reactive, computed } from 'vue'
import IncomeList from '@/components/readList/IncomeList.vue'
import Head from '@/components/readList/Head.vue'
import Form from '@/components/readList/Form.vue'

export default {
  components: { Head, IncomeList, Form },
  setup() {
    const state = reactive({
      income: [],
      sortedIncome: computed(() => {
        let temp = []

        temp = state.income.sort(function (a, b) {
          return b.date - a.date
        })

        return temp
      }),
      totalIncome: computed(() => {
        let temp = 0
        if (state.income.length > 0) {
          for (let i = 0; i < state.income.length; i++) {
            temp += state.income[i].value
          }
          return temp
        } else {
          return 0
        }
      })
    })

    function AddIncome(obj) {
      let d = obj.date.split('-')
      let newD = new Date(d[0], d[1], d[2])

      state.income = [
        ...state.income,
        {
          id: Date.now(),
          desc: obj.desc,
          value: parseInt(obj.value),
          date: newD.getTime()
        }
      ]
    }

    function removeItem(id) {
      state.income = state.income.filter((v) => v.id != id)
    }

    // Return template data
    return {
      Head,
      IncomeList,
      Form,
      state,
      AddIncome,
      removeItem
    }
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Fira Sans', sans-serif;
}

body {
  background: #eee;
}
</style>
