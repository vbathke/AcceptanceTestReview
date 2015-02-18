# UI Test Capture
Plugin para o Jenkins criado para permitir o acompanhamento dos resultados de uma suite de teste de interface, com uma interface simples para visualizar eventuais erros e a Screenshot do ocorrido.



### Acrescentar os atributos
        protected static String testeAtual = "";
        protected static String classeAtual = "";
        private static StringBuilder status = new StringBuilder();
        @Rule public TestName testname = new TestName();

### Na anotação @After, atribuir a classe método atual e gravar a screenshot
                classeAtual = this.getClass().getName();
                testeAtual = this.getClass().getName()+"."+testname.getMethodName();
                File scrFile = ((TakesScreenshot)FabricaWebDriver.getDriver()).getScreenshotAs(OutputType.FILE);
                FileUtils.copyFile(scrFile, new File("target/screenshots/"+testeAtual+".png"));

### Implementar a @Rule TestWatcher para tratar a mensgem da falha conforme abaixo:
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

### Montar a String para alimentar o arquivo target/teststream.txt
        @AfterClass
        public static void tearDownClass() {
                try {
                    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("target/teststream.txt", true)));
                    out.println("{\"metodo\":\""+testeAtual+"\", \"status\":\""+status+"\", \"classe\":\""+classeAtual+"\", \"descricao\":\""+testeAtual+"\"}");
                    out.close();
                } catch (IOException e) {}
        }

### Configuração do Jenkins
Para o correto funcionamento do plugin é importante que o Job esteja configurado com as seguintes diretivas:

