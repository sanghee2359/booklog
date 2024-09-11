<template>
  <div class="container">
    <h2>읽을 책 리스트</h2>
    <div class="input-group" style="margin-bottom: 10px">
      <input
        type="text"
        class="form-control"
        placeholder="앞으로 앍을 책을 입력하세요"
        v-model="str"
        @keyup.enter="createTodo(str)"
      />
      <span class="input-group-btn">
        <button class="btn btn-default" type="button" @click="createTodo(str)">추가</button>
      </span>
    </div>
    <ul class="list-group">
      <li class="list-group-item" v-for="(todo, index) in todos">
        {{ todo.name }}
        <div class="btn-group pull-right" style="font-size: 12px; line-height: 1">
          <button
            type="button"
            class="btn-link dropdown-toggle"
            data-toggle="dropdown"
            aria-haspopup="true"
            aria-expanded="false"
          >
            더보기 <span class="caret"></span>
          </button>
          <ul class="dropdown-menu">
            <li><a href="#" @click="deleteTodo(index)"> 삭제 </a></li>
          </ul>
        </div>
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  name: 'TodoPage',
  data() {
    return {
      str: null,
      todos: [
        {
          name: '밥 먹기'
        },
        {
          name: '공부 하기'
        },
        {
          name: '일하기'
        }
      ]
    }
  },

  methods: {
    deleteTodo: function (index) {
      this.todos.splice(index, 1)
    },

    createTodo: function (val) {
      this.todos.push({ name: val })
    }
  }
}
</script>
