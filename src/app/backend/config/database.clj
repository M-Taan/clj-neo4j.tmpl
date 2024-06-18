(ns backend.config.database
  (:require [neo4j-clj.core :as database]))

(def ^:private db (atom nil))

(defn connect [{:keys [uri username password]}]
  (when (nil? @db)
    (reset! db (database/connect (java.net.URI. uri) username password)))
  true)

(defn disconnect []
  (database/disconnect db)
  (reset! db nil))

(defn get-session []
  (database/get-session @db))

(defn query 
  ([qr]
   (query qr {}))
  ([qr params]
   (with-open [session (get-session)]
     (database/execute session qr params))))

(defmacro defquery
  "Shortcut macro to define a named query."
  [name ^String query]
  `(def ~name (database/create-query ~query)))

(defmacro with-transaction [tx & body]
  `(with-open [~tx (database/transaction (get-session))]
     (try
       (let [r# (do ~@body)]
         (.commit ~tx)
         r#)
       (finally
         (.close ~tx)))))
