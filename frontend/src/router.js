
import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router);



import Customer from "./components/Customer"
import OutOfStockOrder from "./components/OutOfStockOrder"
import MyPage from "./components/MyPage"
import BookManager from "./components/BookManager"

import OrderManager from "./components/OrderManager"

import DeliveryManager from "./components/DeliveryManager"

import CustomerManager from "./components/CustomerManager"

export default new Router({
    // mode: 'history',
    base: process.env.BASE_URL,
    routes: [

            {
                path: '/Customer',
                name: 'Customer',
                component: Customer
            },
            {
                path: '/OutOfStockOrder',
                name: 'OutOfStockOrder',
                component: OutOfStockOrder
            },
            {
                path: '/MyPage',
                name: 'MyPage',
                component: MyPage
            },
            {
                path: '/Book',
                name: 'BookManager',
                component: BookManager
            },

            {
                path: '/Order',
                name: 'OrderManager',
                component: OrderManager
            },

            {
                path: '/Delivery',
                name: 'DeliveryManager',
                component: DeliveryManager
            },

            {
                path: '/Customer',
                name: 'CustomerManager',
                component: CustomerManager
            },



    ]
})
