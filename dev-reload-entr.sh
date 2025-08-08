#!/bin/bash

# ─────────── Verificação de dependências ───────────
if ! command -v inotifywait >/dev/null 2>&1; then
  echo "❌ Erro: o comando 'inotifywait' não está instalado."
  echo "➡️  Instale com: sudo apt install inotify-tools"
  exit 1
fi

# ─────────── Configurações ───────────
WATCH_DIRS=(domain application infra presentation)
SPRING_BOOT_MODULE="spring-boot-run"
OUTHERS_MODULES=$(IFS=,; echo "${WATCH_DIRS[*]}")
SPRING_PID_FILE="/tmp/spring_boot_app.pid"

# ─────────── Funções ───────────

start_spring() {
  echo -e "\n➡️  Iniciando Spring Boot (DevTools RESTART DESATIVADO)..."
  mvn -pl "$SPRING_BOOT_MODULE" spring-boot:run \
      -Dspring.devtools.restart.enabled=false \
      < /dev/null &
  SPRING_PID=$!
  printf "%d" "$SPRING_PID" > "$SPRING_PID_FILE"
  echo "✅ Spring rodando com PID $SPRING_PID"
}

stop_spring() {
  if [[ -f "$SPRING_PID_FILE" ]]; then
    PID=$(<"$SPRING_PID_FILE")
    echo "⛔ Parando Spring Boot (PID $PID)..."
    pkill -TERM -P "$PID" 2>/dev/null
    kill -TERM "$PID" 2>/dev/null
    wait "$PID" 2>/dev/null
    rm -f "$SPRING_PID_FILE"
  fi
}

cleanup() {
  echo -e "\n🧹 Encerrando script..."
  stop_spring
  exit 0
}

rebuild_and_restart() {
  echo -e "\n📝 Alterações detectadas em $(date +'%Y-%m-%d %H:%M:%S')"
  echo "🔧 Instalando módulos alterados: $OUTHERS_MODULES"
  if mvn install -pl "$OUTHERS_MODULES" -am -DskipTests < /dev/null; then
    echo "✅ Build OK — reiniciando Spring..."
    stop_spring
    start_spring
  else
    echo "❌ Build falhou — mantendo instância atual."
  fi
}

# ─────────── Trap de sinais ───────────
trap cleanup INT TERM

# ─────────── Main ───────────
echo -e "\n👁️  Monitorando: ${WATCH_DIRS[*]}"
echo "(Ctrl+C para sair)\n"

start_spring

while inotifywait -e modify,create,delete -r "${WATCH_DIRS[@]}"; do
  rebuild_and_restart
done
