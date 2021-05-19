<template>

<v-data-table
    :headers="headers"
    :items="outOfStockOrder"
    :items-per-page="5"
    class="elevation-1"
  ></v-data-table>

</template>

<script>
  const axios = require('axios').default;

  export default {
    name: 'OutOfStockOrder',
    props: {
      value: Object,
      editMode: Boolean,
      isNew: Boolean
    },
    data: () => ({
        headers: [
            { text: "orderId", value: "orderId" },
            { text: "bookId", value: "bookId" },
            { text: "customerId", value: "customerId" },
            { text: "orderDt", value: "orderDt" },
        ],
        outOfStockOrder : [],
    }),
    async created() {
      var temp = await axios.get(axios.backend + '/outofstockorders')

      this.outOfStockOrder = temp.data._embedded.outofstockorders;

    },
    methods: {
    }
  }
</script>

