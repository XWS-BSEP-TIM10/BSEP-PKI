<template>
  <link
    href="//maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css"
    rel="stylesheet"
    id="bootstrap-css"
  />
  <div class="wrapper fadeInDown" style="margin-top: 1em">
    <div id="formContent">
      <!-- Login Form -->
      <form style="margin-top: 3em" @submit="checkForm">
          <p style="color: red; background-color: white" v-if="errors.length">
    <b>Please correct the following error(s):</b>
    <ul v-for="error in errors" :key="error" style="color: red; background-color: white">
      <li style="margin-left:20%">{{ error }}<br></li><br>
    </ul>
  </p>
        <select
          class="browser-default custom-select"
          style="width: 20em"
          v-model="certificateType"
          disabled
        >
          <option selected hidden value="ROOT">ROOT</option>
        </select>
        <select
          class="browser-default custom-select"
          style="width: 20em; margin-top: 1em"
          v-model="issuer"
          v-if="
            certificateType == 'INTERMEDIATE' || certificateType == 'END_ENTITY'
          "
        >
          <option selected hidden>Issuer</option>
          <option value="ROOT">ROOT</option>
          <option value="INTERMEDIATE">INTERMEDIATE</option>
          <option value="END_ENTITY">END ENTITY</option>
        </select>
        <select
          class="browser-default custom-select"
          style="width: 20em; margin-top: 1em"
          v-model="subject"
        >
          <option selected hidden>Subject</option>
          <option
            v-for="option in allSubjects"
            :value="option.id"
            :key="option.username"
          >
            {{ option.username }}
          </option>
        </select>
        <input
          style="width: 20em; margin-top: 1em"
          type="text"
          class="fadeIn third"
          name="login"
          placeholder="Oragnization"
          v-model="organization"
        />
        <input
          style="width: 20em; margin-top: 1em"
          type="text"
          class="fadeIn third"
          name="login"
          placeholder="Oragnization unit name"
          v-model="organizationUnitName"
        />
        <input
          style="width: 20em; margin-top: 1em"
          type="text"
          class="fadeIn third"
          name="orgEmail"
          placeholder="Organization email"
          v-model="organizationEmail"
        />
        <input
          style="width: 20em; margin-top: 1em"
          type="text"
          class="fadeIn third"
          name="orgEmail"
          placeholder="Country code"
          v-model="countryCode"
        />
        <Datepicker
          v-model="date"
          :minDate = "(new Date()).setDate((new Date()).getDate()+1)"
          style="width: 20em; margin-top: 1em"
          class="fadeIn third"
          id="datePicker"
        ></Datepicker>
        <div style="text-align: left; margin-left: 4.3em;margin-top:1em">Key usages:</div>
        <select
          class="browser-default custom-select"
          style="width: 20em"
          v-model="keyUsages"
          multiple
        >
          <option selected hidden>Certificate type</option>
          <option value="128">Digital signature</option>
          <option value="64">Non repudiation</option>
          <option value="32">Key encipherment</option>
          <option value="16">Data encipherment</option>
          <option value="8">Key agreement</option>
          <option value="2">cRL sign</option>
          <option value="1">Encipher only</option>
          <option value="32768">Decipher only</option>
        </select>
        <div style="text-align: left; margin-left: 4.3em;margin-top:1em">Extended key usages:</div>
        <select
          class="browser-default custom-select"
          style="width: 20em"
          v-model="extendedKeyUsages"
          multiple
        >
          <option selected hidden>Certificate type</option>
          <option value="1">TLS Web server authentication</option>
          <option value="2">TLS Web client authentication</option>
          <option value="3">Sign (downloadable) executable code</option>
          <option value="4">Email protection</option>
          <option value="5">IPSEC End System (host or router)</option>
          <option value="6">IPSEC Tunnel</option>
          <option value="7">IPSEC User</option>
          <option value="8">Timestamping</option>
        </select>
        <input
          type="button"
          class="fadeIn fourth"
          value="Create certificate"
          style="background-color: rgb(3, 20, 50); margin-top: 1em"
          v-on:click="createCertificate()"
        />
      </form>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import Datepicker from '@vuepic/vue-datepicker'
import '@vuepic/vue-datepicker/dist/main.css'

export default {
  name: 'CreateCertficateView',
  components: { Datepicker },
  data: function () {
    return {
      certificateType: 'ROOT',
      subject: 'Subject',
      issuer: 'Issuer',
      organization: '',
      organizationUnitName: '',
      organizationEmail: '',
      countryCode: '',
      allSubjects: [],
      date: null,
      keyUsages: [],
      extendedKeyUsages: [],
      errors: []
    }
  },
  mounted: function () {
    axios.defaults.headers.common.Authorization =
      'Bearer ' + window.sessionStorage.getItem('jwt')
    axios
      .get('https://localhost:8080/api/v1/users/getAllUsers')
      .then((response) => {
        this.allSubjects = response.data
      })
  },
  methods: {
    formatDate: function (date) {
      const d = new Date(date)
      let month = '' + (d.getMonth() + 1)
      let day = '' + d.getDate()
      const year = d.getFullYear()

      if (month.length < 2) month = '0' + month
      if (day.length < 2) day = '0' + day

      return [year, month, day].join('-')
    },
    createCertificate: function () {
      this.checkForm()
      const newCertificate = {
        subjectUID: Number(this.subject),
        organization: this.organization,
        organizationalUnitName: this.organizationUnitName,
        organizationEmail: this.organizationEmail,
        countryCode: this.countryCode,
        issuerCertificateId: this.issuer,
        endDate: this.formatDate(this.date),
        certificateType: this.certificateType,
        keyUsages: this.keyUsages,
        extendedKeyUsages: this.extendedKeyUsages
      }
      axios.defaults.headers.common.Authorization =
        'Bearer ' + window.sessionStorage.getItem('jwt')
      axios
        .post('https://localhost:8080/api/v1/certificate/create', newCertificate)
        .then((response) => {
          alert('Success')
        })
    },
    checkForm: function (e) {
      this.errors = []

      if (!this.certificateType) {
        this.errors.push('Certificate Type required.')
      }
      if (!this.subject || this.subject === 'Subject') {
        this.errors.push('Subject required.')
      }
      if (!this.organization) {
        this.errors.push('Organization required.')
      }
      if (!this.organizationUnitName) {
        this.errors.push('Organization unit name required.')
      }
      if (!this.organizationEmail) {
        this.errors.push('Organization email required.')
      }
      if (!this.countryCode) {
        this.errors.push('Country code required.')
      }
      if (!this.date) {
        this.errors.push('Date required.')
      }
      e.preventDefault()
    }
  }
}
</script>
<style scoped src="@/css/Admin.css"></style>
<style scoped src="@/css/Login.css"></style>
