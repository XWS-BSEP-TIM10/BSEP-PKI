<template>
  <transition name="modal-fade">
    <div centered class="modal-backdrop">
      <div class="modal">
        <header class="modal-header">
          <slot name="header"> Create certificate </slot>
          <button type="button" class="btn-close" v-on:click="close()">
            x
          </button>
        </header>

        <section class="modal-body">
          <slot name="body">
            <select
              class="form-select"
              aria-label="Default select example"
              v-model="certificateType"
              v-on:change="checkCertificateType()"
              style="width: 70%; margin-left: 15%"
            >
              <option selected hidden>Certificate type</option>
              <option value="INTERMEDIATE">INTERMEDIATE</option>
              <option value="END_ENTITY">END ENTITY</option>
            </select>
            <div style="color:red; margin-left:15%">{{errors[0]}}</div>
            <select
              class="form-select"
              style="width: 70%; margin-top: 1em; margin-left: 15%"
              v-model="subject"
              v-on:change="checkSubject()"
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
            <div style="color:red; margin-left:15%">{{errors[1]}}</div>
            <input
              style="width: 70%; margin-top: 1em; margin-left: 15%"
              type="text"
              class="form-control"
              name="login"
              placeholder="Oragnization"
              v-model="organization"
               v-on:input="checkOrganization()"
            />
            <div style="color:red; margin-left:15%">{{errors[2]}}</div>
            <input
              style="width: 70%; margin-top: 1em; margin-left: 15%"
              type="text"
              class="form-control"
              name="login"
              placeholder="Oragnization unit name"
              v-model="organizationUnitName"
              v-on:input="checkOrganizationUnitName()"
            />
            <div style="color:red; margin-left:15%">{{errors[3]}}</div>
            <input
              style="width: 70%; margin-top: 1em; margin-left: 15%"
              type="text"
              class="form-control"
              name="orgEmail"
              placeholder="Organization email"
              v-model="organizationEmail"
              v-on:input="checkOrganizationEmail()"
            />
            <div style="color:red; margin-left:15%">{{errors[4]}}</div>
            <input
              style="width: 70%; margin-top: 1em; margin-left: 15%"
              type="text"
              class="form-control"
              name="orgEmail"
              placeholder="Country code"
              v-model="countryCode"
              v-on:input="checkCountryCode()"
            />
             <div style="color:red; margin-left:15%">{{errors[5]}}</div>
            <Datepicker
              :maxDate="issuerExpirationDate"
              :minDate = "(new Date()).setDate((new Date()).getDate()+1)"
              v-model="date"
              style="width: 70%; margin-top: 1em; margin-left: 15%"
              class="fadeIn third"
              id="datePicker"
              @update:modelValue="checkDate()"
            ></Datepicker>
             <div style="color:red; margin-left:15%">{{errors[6]}}</div>
            <div style="text-align: left; margin-left: 15.5%; margin-top: 3%">
              Key usages:
            </div>
            <select
              class="form-select"
              style="width: 70%; margin-top: 0.5em; margin-left: 15%"
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
            <div style="text-align: left; margin-left: 15.5%; margin-top: 1em">Extended key usages:</div>
        <select
          class="form-select"
          style="width: 23.5em;margin-left:15%;margin-top:0.5em"
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
              style="
                margin-top: 1em;
                margin-left: 34%;
                background-color: rgb(3, 20, 50);
                border-color: rgb(3, 20, 50);
              "
              type="button"
              class="btn btn-primary"
              value="Create certificate"
              v-on:click="createCertificate()"
            />
          </slot>
        </section>
      </div>
    </div>
  </transition>
</template>
<script>
import axios from 'axios'
import Datepicker from '@vuepic/vue-datepicker'
import '@vuepic/vue-datepicker/dist/main.css'
export default {
  name: 'IssueCertificateModal',
  props: ['issuerCertificateSerialNumber', 'issuerExpirationDate'],
  components: { Datepicker },
  data: function () {
    return {
      certificateType: 'Certificate type',
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
    close () {
      this.$emit('close')
    },
    createCertificate: function () {
      this.checkForm()
      const newCertificate = {
        subjectUID: Number(this.subject),
        organization: this.organization,
        organizationalUnitName: this.organizationUnitName,
        organizationEmail: this.organizationEmail,
        countryCode: this.countryCode,
        issuerCertificateSerialNumber: this.issuerCertificateSerialNumber,
        endDate: this.formatDate(this.date),
        certificateType: this.certificateType,
        keyUsages: this.keyUsages,
        extendedKeyUsages: this.extendedKeyUsages
      }
      axios
        .post('https://localhost:8080/api/v1/certificate/create', newCertificate)
        .then((_response) => {
          window.location.reload()
        })
    },
    checkForm: function (e) {
      this.errors = []

      this.checkCertificateType()
      this.checkSubject()
      this.checkOrganization()
      this.checkOrganizationUnitName()
      this.checkOrganizationEmail()
      this.checkCountryCode()
      this.checkDate()
      for (const element of this.errors) {
        if (element) e.preventDefault()
      }
    },

    checkCertificateType: function () {
      if (!this.certificateType || this.certificateType === 'Certificate type') {
        this.errors[0] = 'Certificate Type required.'
      } else { this.errors[0] = '' }
    },
    checkSubject: function () {
      if (!this.subject || this.subject === 'Subject') {
        this.errors[1] = 'Subject required.'
      } else { this.errors[1] = '' }
    },
    checkOrganization: function () {
      if (!this.organization) {
        this.errors[2] = 'Organization required.'
      } else { this.errors[2] = '' }
    },
    checkOrganizationUnitName: function () {
      if (!this.organizationUnitName) {
        this.errors[3] = 'Organization unit name required.'
      } else { this.errors[3] = '' }
    },
    checkOrganizationEmail: function () {
      if (!this.organizationEmail) {
        this.errors[4] = 'Organization email required.'
      } else { this.errors[4] = '' }
    },
    checkCountryCode: function () {
      if (!this.countryCode) {
        this.errors[5] = 'Country code required.'
      } else { this.errors[5] = '' }
    },
    checkDate: function () {
      if (!this.date) {
        this.errors[6] = 'Date required.'
      } else { this.errors[6] = '' }
    }
  }
}
</script>
<style>
.modal-backdrop {
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: rgba(0, 0, 0, 0.3);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal {
  margin-top: 18%;
  background: #ffffff;
  box-shadow: 2px 2px 20px 1px;
  overflow-x: auto;
  display: flex;
  flex-direction: column;
  height: 85%;
  width: 30%;
  position: relative;
  top: 25%;
  transform: translateY(-50%);
}
.modal-header,
.modal-footer {
  padding: 15px;
  display: flex;
}

.modal-header {
  position: relative;
  border-bottom: 1px solid #eeeeee;
  color: rgb(3, 20, 50);
  justify-content: space-between;
}

.modal-footer {
  border-top: 1px solid #eeeeee;
  flex-direction: column;
  justify-content: flex-end;
}

.modal-body {
  position: relative;
  padding: 20px 10px;
}

.btn-close {
  position: absolute;
  top: 0;
  right: 0;
  border: none;
  font-size: 20px;
  padding: 10px;
  cursor: pointer;
  font-weight: bold;
  color: #4aae9b;
  background: transparent;
}

.btn-green {
  color: white;
  background: #4aae9b;
  border: 1px solid #4aae9b;
  border-radius: 2px;
}
</style>
