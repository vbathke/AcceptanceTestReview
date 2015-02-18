# UI Test Capture
Plugin para o Jenkins visualizar a evolução dos resultados dos testes de uma Build corrente e o histórico de testes de builds anteriores. Com uma interface simples, permite visualizar facilmente a ocorrência de erros e a Screenshot do ocorrido.

Este plugin foi escrito para um projeto de testes na linguagem Java, com a API Selenium e utilizando Maven, mas pode ser adaptado para outras linguagens e APIs de testes desde que obedeça o formato de informações exigido pelo arquivo target/testestream.txt.

Para o funcionamento do plugin, instrumente sua suite com as recomendações abaixo:

### 1. Acrescentar os atributos abaixo em uma classe que todos os seus testes herdem
        protected static String testeAtual = "";
        protected static String classeAtual = "";
        private static StringBuilder status = new StringBuilder();
        @Rule public TestName testname = new TestName();

### 2. No método com a anotação @After, onter a classe e método de teste atual atual e gravar a screenshot do teste
        classeAtual = this.getClass().getName();
        testeAtual = this.getClass().getName()+"."+testname.getMethodName();
        File scrFile = ((TakesScreenshot)FabricaWebDriver.getDriver()).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(scrFile, new File("target/screenshots/"+testeAtual+".png"));

### 3. Implementar a @Rule TestWatcher para tratar a mensgem da falha conforme abaixo:
        @Rule
        public TestWatcher watcher = new TestWatcher() {
            @Override
            protected void failed(Throwable e, Description description) {
                status.append("falha");
            }

            @Override
            protected void succeeded(Description description) {
                status.append("sucesso");
            }

            @Override
            protected void skipped(AssumptionViolatedException e, Description description) {
                status.append("skiped");
            }
        };

### 4. Montar a String para alimentar o arquivo target/teststream.txt
        @AfterClass
        public static void tearDownClass() {
                try {
                    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("target/teststream.txt", true)));
                    out.println("{\"metodo\":\""+testeAtual+"\", \"status\":\""+status+"\", \"classe\":\""+classeAtual+"\", \"descricao\":\""+testeAtual+"\"}");
                    out.close();
                } catch (IOException e) {}
        }

### 4. Configuração do Jenkins
Para o correto funcionamento do plugin é importante que o Job esteja configurado com as seguintes diretivas:

        1. Antes de invocar o Goal 'test' deve ser invocado o Goal 'clean'
        2. Adicione um Post-build Action de 'Arquive artefacts' com o seguinte valor: target/surefire-reports/**/*, target/screenshots/**/*, target/teststream.txt

