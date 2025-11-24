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

## 👩‍💻 Equipe de Desenvolvimento

| Integrante         | Função | Responsabilidades |
|-------------------|---------|-------------------|
| Silvio Marques    | Desenvolvedor(a) Android | Implementação da UI, Navigation, ViewModels |
| Edvaldo Luiz      | Desenvolvedor(a) Backend/API | Integração e endpoints REST |
| nyckollas Paulino | Designer UX/UI | Protótipos, acessibilidade e responsividade |
| Clara Menezes     | Tester / Documentação | Testes, README, organização do repositório |

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
