import 'reflect-metadata'

import { createApp } from 'vue'
import App from './App.vue'
import router from '@/router'
import './assets/tailwind.css' /** Tailwind 스타일 적용 **/
import '@/assets/styles.scss'
import Aura from '@primevue/themes/aura'
import PrimeVue from 'primevue/config'
import ConfirmationService from 'primevue/confirmationservice'
import ToastService from 'primevue/toastservice'
import './assets/main.css'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import 'bootstrap/dist/css/bootstrap-utilities.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
const app = createApp(App)
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
app.use(router)
app.use(PrimeVue, {
  theme: {
    preset: Aura,
    options: {
      darkModeSelector: '.app-dark'
    }
  }
})

app.use(ToastService)
app.use(ConfirmationService)

app.use(ElementPlus)
app.mount('#app')
app.component('Toast', Toast)
