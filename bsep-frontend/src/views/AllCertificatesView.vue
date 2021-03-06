<template>
  <table
    v-for="cert in certificates"
    :key="cert.serialNumber"
    style="
      border: 1px solid gray;
      width: 50%;
      margin-left: 25%;
      margin-top: 5%;
      background: white;
      zoom: 1;
    "
    class="table table-striped"
  >
    <thead style="border: 1px solid gray">
      <tr>
        <th id="header1"></th>
        <th id="header2">Certificate</th>
        <th id="header3"></th>
        <th id="header4">Subject</th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <th id="certType">Certificate type:</th>
        <td>{{ cert.certificateType }}</td>
        <th id="username">Username:</th>

        <td>{{ cert.username }}</td>
        <!--Subject data-->
      </tr>
      <tr>
        <th id="serNumber">Serial number:</th>
        <td>{{ cert.serialNumber }}</td>
        <th id="country">Country:</th>

        <td>{{ cert.countryCode }}</td>
      </tr>
      <tr>
        <th id="startDate">Start date:</th>
        <td>{{ cert.startDate }}</td>
        <th id="orgUnit">Organization unit:</th>

        <td>{{ cert.organization }}</td>
      </tr>
      <tr>
        <th id="expDate">Expiration date:</th>
        <td>{{ cert.endDate }}</td>
        <th id="orgName">Organization name:</th>

        <td>{{ cert.organizationalUnitName }}</td>
      </tr>
      <tr v-bind:class="cert.status == 'VALID' ? 'green' : 'red'">
        <th id="certStatus">Certificate status:</th>
        <td>{{ cert.status }}</td>
        <th id="issuerCommonName">Issuer common name</th>

        <td>{{ cert.issuerCommonName }}</td>
      </tr>
      <tr>
        <td>
          <button
            type="button"
            class="btn btn-primary"
            style="
              background-color: rgb(3, 20, 50);
              border-color: rgb(3, 20, 50);
              width: 100%;
            "
            v-on:click="isRevoked(cert.serialNumber)"
          >
            Is revoked
          </button>
        </td>
        <td>
          <button
            type="button"
            class="btn btn-primary"
            style="
              background-color: rgb(3, 20, 50);
              border-color: rgb(3, 20, 50);
              width: 70%;
            "
            v-if="cert.status != 'REVOKED' && role === 'ROLE_ADMIN'"
            v-on:click="revokeCertificate(cert.serialNumber)"
          >
            Revoke certificate
          </button>
        </td>
        <td>
          <button
            type="button"
            class="btn btn-primary"
            style="
              background-color: rgb(3, 20, 50);
              border-color: rgb(3, 20, 50);
            "
            v-on:click="showModal(cert.serialNumber, cert.endDate)"
            v-if="
              cert.certificateType != 'END_ENTITY' && cert.status == 'VALID'
            "
          >
            Issue certificate
          </button>
        </td>
        <td>
          <button
            type="button"
            class="btn btn-primary"
            style="
              background-color: rgb(3, 20, 50);
              border-color: rgb(3, 20, 50);
              width: 110%;
              margin-left: -10%;
            "
            v-on:click="downloadCertificate(cert.id)"
          >
            Download certificate
          </button>
        </td>
      </tr>
    </tbody>
  </table>
  <IssueCertificateModal
    v-show="isModalVisible"
    @close="closeModal"
    v-bind:issuerCertificateSerialNumber="issuerCertificateSerialNumber"
    v-bind:issuerExpirationDate="issuerExpirationDate"
  />
  <RevokeCertificateModal
    v-show="isRevokeModalVisible"
    @close="closeRevokeModal"
    v-bind:serialNumber="serialNumber"
  />
</template>

<script>
import axios from 'axios'
import IssueCertificateModal from '@/components/IssueCertificateModal.vue'
import RevokeCertificateModal from '@/components/RevokeCertificateModal.vue'
export default {
  name: 'AllCertificatesView',
  components: { IssueCertificateModal, RevokeCertificateModal },
  data: function () {
    return {
      isModalVisible: false,
      certificates: [],
      issuerCertificateSerialNumber: '',
      issuerExpirationDate: null,
      role: '',
      isRevokeModalVisible: false,
      serialNumber: ''
    }
  },
  mounted: function () {
    const jwtToken = window.sessionStorage.getItem('jwt')
    if (jwtToken) {
      const tokenSplit = jwtToken.split('.')
      const decoded = decodeURIComponent(escape(window.atob(tokenSplit[1])))
      const obj = JSON.parse(decoded)
      console.log(obj.role)
      this.role = obj.role
    }

    if (this.role == null) this.role = ''
    else if (this.role === 'ROLE_USER') {
      axios.defaults.headers.common.Authorization =
        'Bearer ' + window.sessionStorage.getItem('jwt')
      axios
        .get('https://localhost:8080/api/v1/certificate')
        .then((response) => {
          this.certificates = response.data
        })
    }
    axios.defaults.headers.common.Authorization =
      'Bearer ' + window.sessionStorage.getItem('jwt')
    axios.get('https://localhost:8080/api/v1/certificate/all').then((response) => {
      this.certificates = response.data
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
    showModal (serialNumber, expirationDate) {
      this.issuerExpirationDate = expirationDate
      this.issuerCertificateSerialNumber = serialNumber
      this.isModalVisible = true
    },
    downloadCertificate (id) {
      axios.defaults.headers.common.Authorization =
        'Bearer ' + window.sessionStorage.getItem('jwt')
      axios
        .get('https://localhost:8080/api/v1/certificate/' + id + '/download')
        .then((response) => {
          const blob = new Blob([response.data], {
            type: 'application/x-x509-ca-cert'
          })
          const url = window.URL.createObjectURL(blob)
          window.open(url)
        })
    },
    closeModal () {
      this.isModalVisible = false
    },
    revokeCertificate (serialNumber) {
      this.serialNumber = serialNumber
      this.showRevokeModal(123, 123)
    },
    isRevoked (serialNumber) {
      axios.defaults.headers.common.Authorization =
        'Bearer ' + window.sessionStorage.getItem('jwt')
      axios
        .get(
          'https://localhost:8080/api/v1/certificate/' +
            serialNumber +
            '/status'
        )
        .then((response) => {
          if (response.data.status) {
            alert('Certificate is revoked: ' + response.data.revocationReason)
          } else {
            alert('Certifivate is not revoked')
          }
        })
    },
    showRevokeModal (serialNumber, expirationDate) {
      this.issuerExpirationDate = expirationDate
      this.issuerCertificateSerialNumber = serialNumber
      this.isRevokeModalVisible = true
    },
    closeRevokeModal () {
      this.isRevokeModalVisible = false
    }
  }
}
</script>
<style scoped src="@/css/Admin.css"></style>
<style scoped src="@/css/Login.css"></style>
