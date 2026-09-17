1. Porque todas as telas ficam declaradas em um lugar apenas. O compilador conhece todas as opções, então não tem string de rota pelo código com risco de erro de digitação.

2. Porque a AddWordScreen só precisa mostrar esse valor, e ele é uma String simples. Mandando pela rota a tela recebe o que precisa sem depender da HomeScreen nem de um ViewModel compartilhado.
Usei argumento opcional (?currentText=) com defaultValue "" porque no começo a string é vazia, e argumento de caminho (/{currentText}) não aceita valor vazio.

4. Porque a volta é feita com popBackStack(), sem navigate(), então a HomeScreen que já está na pilha é reaproveitada. A AddWordScreen grava só a palavra no savedStateHandle da tela anterior (previousBackStackEntry) e a HomeScreen observa essa chave com getStateFlow. Esse valor também sobrevive à rotação e à morte do processo.
   
5. Porque quando a AddWordScreen abre, a HomeScreen sai da composição. Com remember a string acumulada seria perdida e com rememberSaveable o NavHost guarda o estado da tela.
