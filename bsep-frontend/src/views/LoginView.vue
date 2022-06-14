<template>
  <div class="wrapper fadeInDown">
    <div v-if="!enabled2FA" id="formContent" style="margin-top: 10%; margin-bottom: 20.25%">
      <!-- Login Form -->
      <form style="margin-top: 10%;">
        <input
          type="text"
          class="fadeIn second"
          name="login"
          placeholder="login"
          v-model="username"
        />
        <input
          type="password"
          class="fadeIn third"
          name="login"
          placeholder="password"
          v-model="password"
        />
        <div style="color:red">{{error}}</div>
        <input
          type="button"
          class="fadeIn fourth"
          value="Log In"
          style="background-color: rgb(3, 20, 50)"
          v-on:click="login()"
        />
      </form>
    </div>

     <div v-else id="formContent" style="margin-top: 10%; margin-bottom: 20.25%">
      <!-- Login Form -->
      <form style="margin-top: 10%;">
        <input
          type="text"
          class="fadeIn second"
          name="Code"
          placeholder="code"
          v-model="inputCode"
        />
        <div style="color:red">{{error}}</div>
        <input
          type="button"
          class="fadeIn fourth"
          value="Log In"
          style="background-color: rgb(3, 20, 50)"
          v-on:click="login2FA()"
        />
      </form>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'LoginView',
  data: function () {
    return {
      username: '',
      password: '',
      error: '',
      enabled2FA: false,
      inputCode: '',
      enteringCode: false
    }
  },
  mounted: function () {},
  methods: {
    login: function () {
      const user = {
        username: this.username,
        password: this.password,
        code: ''
      }
      /*  axios
        .post('https://localhost:8080/api/v1/auth/login', user)
        .then((response) => {
          this.error = ''
          window.sessionStorage.setItem('jwt', response.data.jwt)
          this.$router.push('/all-certificates-page')
        }).catch((err) => {
          console.log(err)
          this.error = 'Oops! The username and password combination is incorrect. Please try again!'
        }) */
      axios
        .post('https://localhost:8080/api/v1/auth/login-check', user)
        .then((response) => {
          this.enabled2FA = response.data
          if (!this.enabled2FA) {
            this.regularLogin()
          } else {
            this.enteringCode = true
            this.error = ''
          }
        }).catch((err) => {
          console.log(err)
          this.error = 'Oops! The username and password combination is incorrect. Please try again!'
        })
    },
    regularLogin: function () {
      const user = {
        username: this.username,
        password: this.password,
        code: this.inputCode
      }
      axios
        .post('https://localhost:8080/api/v1/auth/login', user)
        .then((response) => {
          this.error = ''
          window.sessionStorage.setItem('jwt', response.data.jwt)
          this.$router.push('/all-certificates-page')
        }).catch((err) => {
          console.log(err)
          if (!this.enteringCode) this.error = 'Oops! The username and password combination is incorrect. Please try again!'
          else this.error = 'Oops! Wrong code.'
        })
    },
    login2FA: function () {
      this.regularLogin()
    }
  }
}
</script>
<style scoped src="@/css/Login.css"></style>
