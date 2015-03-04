# UI Test Capture
Plugin do Jenkins feito para visualizar ao vivo os resultados dos testes de uma Build em construção e o histórico de testes de builds anteriores. Com uma interface simples, permite visualizar a ocorrência de erros e a Screenshot do ocorrido.

Este plugin foi escrito para um projeto de testes na linguagem Java, com a API Selenium e utilizando Maven, mas pode ser adaptado para outras linguagens e APIs de testes desde que obedeça o formato de informações exigido pelo arquivo target/testestream.txt e grave as screenshots no diretório target/screenshots com o nome de arquivo convencionado.

Para o funcionamento do plugin, instrumente sua suite com as recomendações abaixo:

### 1. Ao final do teste correntem, gravar a screenshot do teste com o caminho completo da Classe e método de origem, ex.: "br.vbathke.Class.method.png"
        classeAtual = this.getClass().getName();
        testeAtual = this.getClass().getName()+"."+testname.getMethodName();
        File scrFile = ((TakesScreenshot)FabricaWebDriver.getDriver()).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(scrFile, new File("target/screenshots/"+testeAtual+".png"));

### 2. Implementar a @Rule TestWatcher para tratar a mensgem da falha conforme abaixo:
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

### 3. Montar a String para alimentar o arquivo target/teststream.txt
        @AfterClass
        public static void tearDownClass() {
                try {
                    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("target/teststream.txt", true)));
                    out.println("{\"metodo\":\""+testeAtual+"\", \"status\":\""+status+"\", \"classe\":\""+classeAtual+"\", \"descricao\":\""+testeAtual+"\"}");
                    out.close();
                } catch (IOException e) {}
        }

### 4. Configuração do Jenkins
Para o correto funcionamento do plugin é importante que o Job esteja configurado com os seguintes parâmetros:

        1. No Maven, antes de invocar o Goal 'test', configure o Goal 'clean' para garantir a limpeza das execuções anteriores.
        2. Adicione um Post-build Action de 'Arquive Artifacts' com o seguinte valor: target/surefire-reports/**/*, target/screenshots/**/*, target/teststream.txt

