# EcoScan 🌱♻️

> **Sustentabilidade na palma da mão.**
> Aplicativo Android nativo para auxiliar no consumo consciente, identificando a reciclabilidade e o impacto ambiental de produtos através do código de barras.

![Badge em Desenvolvimento](http://img.shields.io/static/v1?label=STATUS&message=EM%20DESENVOLVIMENTO&color=GREEN&style=for-the-badge)
![Badge Kotlin](https://img.shields.io/static/v1?label=LINGUAGEM&message=KOTLIN&color=blue&style=for-the-badge)
![Badge Android](https://img.shields.io/static/v1?label=PLATAFORMA&message=ANDROID&color=green&style=for-the-badge)

---

## 📋 Sobre o Projeto

O **EcoScan** foi desenvolvido como projeto acadêmico para a disciplina de Desenvolvimento Mobile. O objetivo é resolver a dificuldade dos consumidores em entender o impacto ambiental dos produtos que compram e como descartar corretamente as embalagens.

O app consome a API pública **Open Food Facts** para analisar:
* **Eco-Score:** Classificação de impacto ambiental (A a E).
* **Materiais:** Identificação da composição da embalagem (Plástico, Papel, Vidro, etc.).
* **Descarte:** Orientações sobre qual lixeira utilizar.

---

## 🚀 Funcionalidades

* [x] **Leitura de Código de Barras:** Uso da câmera para escanear produtos (via ML Kit ou Zxing).
* [x] **Consulta de API:** Busca dados em tempo real na base global Open Food Facts.
* [x] **Semáforo Ambiental:** Interface visual que indica o nível de sustentabilidade (Verde/Amarelo/Vermelho).
* [x] **Histórico de Scan:** Lista persistente dos últimos produtos verificados.
* [x] **Acessibilidade:** Suporte a leitores de tela (TalkBack) e alto contraste.

---

## 🛠 Tecnologias Utilizadas

O projeto segue as melhores práticas de desenvolvimento Android moderno (Modern Android Development - MAD):

* **Linguagem:** [Kotlin](https://kotlinlang.org/)
* **Arquitetura:** MVVM (Model-View-ViewModel)
* **Injeção de Dependência:** (Manual ou Hilt - *especifique se usar*)
* **Comunicação Assíncrona:** Coroutines & Flow
* **Rede:** [Retrofit 2](https://square.github.io/retrofit/) + Gson
* **Navegação:** Jetpack Navigation Component + Safe Args
* **Imagens:** [Coil](https://coil-kt.github.io/coil/)
* **UI:** ViewBinding e Material Design 3
* **API Externa:** [Open Food Facts API](https://world.openfoodfacts.org/data)

---

## 📂 Estrutura do Projeto

A organização das pastas segue a separação de responsabilidades (Clean Architecture simplificada):

```text
com.seugrupo.ecoscan
├── data                # Camada de Dados
│   ├── model           # Data Classes (JSON response)
│   ├── remote          # Interfaces Retrofit e Client HTTP
│   └── repository      # Fonte única de verdade (Single Source of Truth)
├── ui                  # Camada de Interface (Views e ViewModels)
│   ├── scan            # Fragmento de Scanner
│   ├── details         # Fragmento de Detalhes do Produto
│   └── history         # Fragmento de Histórico
└── utils               # Extensões e constantes
👥 Equipe de DesenvolvimentoNomeFunçãoGitHubSeu Nome 1Desenvolvedor (UI/UX & Fragments)@usuario1Seu Nome 2Desenvolvedor (API & Repository)@usuario2Seu Nome 3Desenvolvedor (ViewModel & Lógica)@usuario3Seu Nome 4Desenvolvedor (QA & Documentação)@usuario4🔧 Como Rodar o ProjetoPré-requisitosAndroid Studio (Versão Jellyfish ou superior).JDK 17 ou superior.Dispositivo Android ou Emulador com API 26+.Passo a PassoClone o repositório:Bashgit clone [https://github.com/SEU_USUARIO/EcoScan.git](https://github.com/SEU_USUARIO/EcoScan.git)
Abra no Android Studio:Selecione a pasta raiz do projeto clonado.Sincronize o Gradle:Aguarde o download das dependências.Configure a API (Opcional):O projeto usa a API pública, não requer chave para testes básicos.Execute:Clique no botão Run (Shift + F10).🤝 Contribuição e Fluxo GitUtilizamos o padrão de Feature Branch:Crie uma branch para sua tarefa (git checkout -b feature/nova-funcionalidade).Faça o commit das suas alterações (git commit -m 'feat: Adiciona nova funcionalidade').Faça o push para a branch (git push origin feature/nova-funcionalidade).Abra um Pull Request para a branch main.
