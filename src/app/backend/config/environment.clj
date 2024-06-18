(ns backend.config.environment
  (:require
   [cprop.core :refer [load-config]]
   [cprop.source :as source]))

(def ^:private environment-variables (atom {}))

(defn load-env []
  (reset! environment-variables (load-config
                                 :merge
                                 [(source/from-system-props)
                                  (source/from-env)])))

(defn env
  ([] @environment-variables)
  ([k] (get-in @environment-variables (if (coll? k)
                                        k
                                        [k]))))
