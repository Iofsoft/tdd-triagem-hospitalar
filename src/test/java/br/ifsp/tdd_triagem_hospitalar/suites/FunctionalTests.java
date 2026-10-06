package br.ifsp.tdd_triagem_hospitalar.suites;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite(failIfNoTests = false)
@SelectPackages({"br.ifsp.tdd_triagem_hospitalar.domain", "br.ifsp.tdd_triagem_hospitalar.application"})
@SuiteDisplayName("Testes funcionais")
@IncludeTags({"Functional"})
public class FunctionalTests { }
