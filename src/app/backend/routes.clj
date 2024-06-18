(ns backend.routes
  (:require [backend.api.hello-world :as hello-world]))

(def routes
  ["/api"
   ["/hello-world" {:get hello-world/hello-world-handler}]])
