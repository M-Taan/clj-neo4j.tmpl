(ns backend.api.hello-world)

(defn hello-world-handler [req]
  {:status 200
   :body "Hello World!"})
