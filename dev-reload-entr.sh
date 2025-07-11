#!/bin/bash

# Caminho dos diretórios monitorados
WATCH_DIRS="domain application infra"
PRESENTATION_MODULE="presentation"

# Arquivo temporário com lista dos arquivos para o entr
TMP_FILE_LIST=$(mktemp)
# Arquivo para armazenar o PID da JVM
SPRING_PID_FILE="/tmp/spring_boot_app.pid"

# Encontra todos os arquivos fonte relevantes
find $WATCH_DIRS -type f \( -name "*.java" -o -name "*.xml" \) > "$TMP_FILE_LIST"

# Função para iniciar a aplicação
start_spring() {
  echo -e "\n➡️  Iniciando aplicação Spring Boot..."
  mvn -pl $PRESENTATION_MODULE spring-boot:run &
  echo $! > "$SPRING_PID_FILE"
  echo "✅ Aplicação rodando com PID $(cat $SPRING_PID_FILE)"
}

# Função para parar a aplicação
stop_spring() {
  if [[ -f "$SPRING_PID_FILE" ]]; then
    PID=$(cat "$SPRING_PID_FILE")
    echo "⛔ Encerrando aplicação Spring Boot (PID $PID)..."
    kill -TERM "$PID" 2>/dev/null
    wait "$PID" 2>/dev/null
    rm -f "$SPRING_PID_FILE"
  fi
}

# Tratamento de encerramento ao sair do script
cleanup() {
  echo -e "\n🧹 Encerrando script..."
  stop_spring
  rm -f "$TMP_FILE_LIST"
  exit 0
}
trap cleanup INT TERM

# Inicia a aplicação pela primeira vez
start_spring

# Começa o monitoramento com entr
echo -e "\n👁️  Monitorando alterações em: $WATCH_DIRS"
echo "(Pressione Ctrl+C para sair)\n"

cat "$TMP_FILE_LIST" | entr -r bash -c '
  echo -e "\n📝 Alterações detectadas."
  echo "----------------------------------------"
  echo "🔧 Instalando os módulos alterados..."
  if mvn install -pl domain,application,infra -am -DskipTests; then
    echo "✅ Instalação bem-sucedida."
    echo "🔁 Reiniciando aplicação Spring Boot..."

    if [[ -f "/tmp/spring_boot_app.pid" ]]; then
      PID=$(cat /tmp/spring_boot_app.pid)
      kill -TERM "$PID" 2>/dev/null
      wait "$PID" 2>/dev/null
      rm -f /tmp/spring_boot_app.pid
    fi

    mvn -pl '"$PRESENTATION_MODULE"' spring-boot:run &
    echo $! > /tmp/spring_boot_app.pid
    echo "✅ Aplicação reiniciada com PID $(cat /tmp/spring_boot_app.pid)"
  else
    echo "❌ Falha na instalação. Aplicação não foi reiniciada."
  fi
'

