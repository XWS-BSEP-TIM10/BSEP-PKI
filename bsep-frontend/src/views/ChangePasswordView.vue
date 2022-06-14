<template>

<div class="wrapper fadeInDown">
    <div id="formContent" style="margin-top: 5%;">
      <div class="form-group form-check" style="padding-top:2em">
                    <input v-model="using2FA" formControlName="dislinkt" type="checkbox" class="form-check-input" id="exampleCheck1" style="margin-left: 10%;  padding: 2.3%; margin-top: 2%;">
                    <label class="form-check-label" for="exampleCheck1" style="margin-top: 2%">Enabled two-factor authentication </label>
                    <div v-if="using2FA" style="padding:1em">
                        <label>{{secret}}</label>
                    </div>
                     <input
          type="button"
          class="fadeIn fourth"
          value="Save"
          style="background-color: rgb(3, 20, 50);margin-top:2em"
          v-on:click="change2FAStatus()"
        />
                </div>
   </div>
  </div>
<div class="wrapper fadeInDown">
    <div id="formContent" style=" margin-bottom: 10.25%">
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
          v-on:input="checkPassword()"
        />
        <div v-bind:id="divId">{{passwordStrength}}</div>
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
          :disabled="divId === 'weakStrength'"
        />
      </form>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import zxcvbn from 'zxcvbn'
export default {
  name: 'ChangePasword',
  components: { },
  data: function () {
    return {
      oldPassword: '',
      newPassword: '',
      repeatedNewPassword: '',
      errors: [],
      passwordStrength: '',
      strengthClass: '',
      divId: '',
      using2FA: false,
      secret: ''
    }
  },
  mounted: function () {
    axios.defaults.headers.common.Authorization =
        'Bearer ' + window.sessionStorage.getItem('jwt')
    axios
      .get('https://localhost:8080/api/v1/users/2fa-status')
      .then((response) => {
        this.using2FA = response.data.isEnabled
        this.secret = response.data.secret
      })
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
          this.errors[1] = 'Old password incorrect'
        })
    },
    checkPassword () {
      const result = zxcvbn(this.newPassword)
      let strength = ''
      switch (result.score) {
        case 0: { this.strengthClass = 'alert alert-danger'; strength = 'Worst'; this.divId = 'weakStrength'; break }
        case 1: { this.strengthClass = 'alert alert-danger'; strength = 'Bad'; this.divId = 'weakStrength'; break }
        case 2: { this.strengthClass = 'alert alert-warning'; strength = 'Weak'; this.divId = 'weakStrength'; break }
        case 3: { this.strengthClass = 'alert alert-info'; strength = 'Good'; this.divId = 'goodStrength'; break }
        default: { this.strengthClass = 'alert alert-success'; strength = 'Strong'; this.divId = 'strongStrength'; break }
      }
      this.passwordStrength = 'Strength: ' + strength + ' ' + result.feedback.warning + '. ' + result.feedback.suggestions
    },
    change2FAStatus () {
      axios.defaults.headers.common.Authorization =
        'Bearer ' + window.sessionStorage.getItem('jwt')
      axios
        .put('https://localhost:8080/api/v1/users/2fa-status', { isEnabled: this.using2FA })
        .then((response) => {
          alert('Success')
          this.$router.push('/all-certificates-page')
        })
        .catch((error) => {
          console.log(error)
          alert('Something went wrong. ')
        })
    }
  }
}
</script>
<style scoped src="@/css/Admin.css">
</style>
<style scoped src="@/css/Login.css"></style>
<style scoped>
  #weakStrength{
    color: red;
  }
  #strongStrength{
    color: green;
  }
  #goodStrength{
    color: green;
  }
</style>
