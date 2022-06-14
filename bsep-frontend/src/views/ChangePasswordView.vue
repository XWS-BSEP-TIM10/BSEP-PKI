<template>
<div class="wrapper fadeInDown">
    <div id="formContent" style="margin-top: 10%; margin-bottom: 20.25%">
      <!-- Login Form -->
      <form style="margin-top: 10%;">
        <input
          type="password"
          class="fadeIn second"
          name="oldPassword"
          placeholder="Old password"
          v-model="oldPassword"
        />
        <input
          type="password"
          class="fadeIn third"
          name="newPassword"
          placeholder="New password"
          v-model="newPassword"
        />
         <input
          type="password"
          class="fadeIn third"
          name="repeatedNewPassword"
          placeholder="Repeated new password"
          v-model="repeatedNewPassword"
        />
        <div style="color:red">{{errors[0]}}</div>
        <div style="color:red">{{errors[1]}}</div>
        <input
          type="button"
          class="fadeIn fourth"
          value="Change password"
          style="background-color: rgb(3, 20, 50)"
          v-on:click="changePassword()"
        />
      </form>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
export default {
  name: 'ChangePasword',
  components: { },
  data: function () {
    return {
      oldPassword: '',
      newPassword: '',
      repeatedNewPassword: '',
      errors: []
    }
  },
  mounted: function () {
  },
  methods: {
    changePassword: function (e) {
      this.errors[0] = ''
      this.errors[1] = ''
      if (this.newPassword !== this.repeatedNewPassword) {
        this.errors[0] = 'Passwords must match'
        e.preventDefault()
      }

      const changePasswordDTO = {
        oldPassword: this.oldPassword,
        newPassword: this.newPassword,
        confirmPassword: this.repeatedNewPassword
      }
      axios.defaults.headers.common.Authorization =
        'Bearer ' + window.sessionStorage.getItem('jwt')
      axios
        .put('https://localhost:8080/api/v1/users/change-password', changePasswordDTO)
        .then((response) => {
          alert('Success')
          this.$router.push('/all-certificates-page')
        })
        .catch((error) => {
          console.log(error)
          this.errors[1] = 'Password to weak or old password incorrect'
        })
    }
  }
}
</script>
<style scoped src="@/css/Admin.css"></style>
<style scoped src="@/css/Login.css"></style>
