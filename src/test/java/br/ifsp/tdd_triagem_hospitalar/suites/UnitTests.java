package br.ifsp.tdd_triagem_hospitalar.suites;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SelectPackages({"br.ifsp.tdd_triagem_hospitalar.domain", "br.ifsp.tdd_triagem_hospitalar.application"})
@SuiteDisplayName("Todos os testes unitarios")
@IncludeTags({"UnitTest"})
public class UnitTests { }
