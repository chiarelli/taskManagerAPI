#!/bin/bash

# Caminho dos diretórios monitorados
WATCH_DIRS="domain application infra"
PRESENTATION_MODULE="presentation"

# Arquivo temporário com lista dos arquivos para o entr
TMP_FILE_LIST=$(mktemp)

# Encontra todos os arquivos fonte relevantes
find $WATCH_DIRS -type f \( -name "*.java" -o -name "*.xml" \) > "$TMP_FILE_LIST"

# PID do processo spring-boot:run
SPRING_PID=0

# Função para iniciar a aplicação
start_spring() {
  echo -e "\n➡️  Iniciando aplicação Spring Boot..."
  mvn -pl $PRESENTATION_MODULE spring-boot:run &
  SPRING_PID=$!
  echo "✅ Aplicação rodando com PID $SPRING_PID"
}

# Função para parar a aplicação
stop_spring() {
  if [[ $SPRING_PID -ne 0 ]]; then
    echo "⛔ Encerrando aplicação Spring Boot (PID $SPRING_PID)..."
    kill -TERM "$SPRING_PID"
    wait "$SPRING_PID" 2>/dev/null
    SPRING_PID=0
  fi
}

# Função para recompilar módulos dependentes
recompile_modules() {
  echo -e "\n🔄 Recompilando módulos domain, application e infra..."
  mvn compile -pl domain,application,infra -am
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
  echo "🔧 Compilando os módulos alterados..."
  if mvn compile -pl domain,application,infra -am; then
    echo "✅ Compilação bem-sucedida."
    echo "🔁 Reiniciando aplicação Spring Boot..."
    kill -TERM '"$SPRING_PID"' && wait '"$SPRING_PID"' 2>/dev/null
    mvn -pl '"$PRESENTATION_MODULE"' spring-boot:run &
    SPRING_PID=$!
    echo "✅ Aplicação reiniciada com PID $SPRING_PID"
  else
    echo "❌ Falha na compilação. Aplicação não foi reiniciada."
  fi
'

