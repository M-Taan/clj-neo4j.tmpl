(ns backend.handler
  (:require
   [reitit.ring :as ring]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [muuntaja.core :as m]
   [mount.core :as mount]
   [ring.middleware.params :as params]
   [reitit.ring.coercion :as coercion]
   [backend.routes :refer [routes]]))

(mount/defstate app-routes
  :start
  (ring/ring-handler
   (ring/router
    [routes]
    {:data {:muuntaja   m/instance
            :middleware [params/wrap-params
                         muuntaja/format-middleware
                         coercion/coerce-exceptions-middleware
                         coercion/coerce-request-middleware
                         coercion/coerce-response-middleware]}})
   (ring/create-default-handler)))

(def app #'app-routes)
