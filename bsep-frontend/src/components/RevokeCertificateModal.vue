<template>
  <transition name="modal-fade">
    <div centered class="modal-backdrop">
      <div class="modal">
        <header class="modal-header">
          <slot name="header"> Revoke certificate </slot>
          <button type="button" class="btn-close" v-on:click="close()">
            x
          </button>
        </header>

        <section class="modal-body">
          <slot name="body">
            <div class="form-group">
    <label for="exampleFormControlTextarea1">Revocation reason</label>
    <textarea v-model="reason" class="form-control" id="exampleFormControlTextarea1" rows="6"></textarea>
  </div>
      <input
          type="button"
          class="fourth"
          value="Revoke certificate"
          style="background-color: rgb(3, 20, 50); margin-top: 1em; margin-left:30%; color: white; zoom: 0.8; margin-top:5%"
          v-on:click="revokeCertificate()"
        />
          </slot>
        </section>
      </div>
    </div>
  </transition>
</template>
<script>
import axios from 'axios'
export default {
  name: 'RevokeCertificateModal',
  props: ['serialNumber'],
  data: function () {
    return {
      reason: ''
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
    close () {
      this.$emit('close')
    },
    revokeCertificate: function () {
      axios.defaults.headers.common.Authorization =
        'Bearer ' + window.sessionStorage.getItem('jwt')
      axios
        .put(
          'https://localhost:8080/api/v1/certificate/' +
            this.serialNumber +
            '/revoke', { reason: this.reason }
        )
        .then(() => {
          window.location.reload()
        })
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
  height: 43%;
  width: 30%;
  position: relative;
  top: 5%;
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
<style scoped src="@/css/Admin.css"></style>
<style scoped src="@/css/Login.css"></style>
