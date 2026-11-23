package br.ifsp.tdd_triagem_hospitalar.suites;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SelectPackages("br.ifsp.tdd_triagem_hospitalar.infrastructure.repository")
@IncludeTags("PersistenceTest")
@SuiteDisplayName("Persistence tests")
public class PersistenceTests { }