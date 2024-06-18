(ns backend.core
  (:require
   [mount.core :as mount]
   [org.httpkit.server :as server]
   [backend.nrepl :as nrepl]
   [backend.config.environment :refer [load-env env]]
   [backend.handler :refer [app]]
   [backend.config.database :as db]
   [taoensso.timbre :as log]))

(mount/defstate ^{:on-reload :noop} http-server
  :start
  (server/run-server #'app)
  :stop
  (http-server))

(mount/defstate ^{:on-reload :noop} repl-server
  :start
  (when (env :nrepl-port)
    (nrepl/start {:bind (env :nrepl-bind)
                  :port (env :nrepl-port)}))
  :stop
  (when repl-server
    (nrepl/stop repl-server)))

(defn- stop-app []
  (mount/stop)
  (shutdown-agents))

(defn- connect-database []
  (when (some empty? [(env :db-uri)
                      (env :db-username)
                      (env :db-password)])
    (log/fatal "[Core][Connect Database] Failed to connect to database due to missing environmet configurations")
    (System/exit 1))
  (db/connect {:uri (env :db-uri)
               :username (env :db-username)
               :password (env :db-password)}))

(defn- start-app [args]
  (mount/start)
  (load-env)
  (connect-database)
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))

(defn -main [& args]
  (start-app args))
