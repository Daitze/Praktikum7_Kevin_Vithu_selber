package contacttracer;

import com.sun.tools.javac.Main;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import static com.tngtech.archunit.lang.conditions.ArchConditions.beAnnotatedWith;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packagesOf = ContacttracerApplication.class)
public class ArchitectureTest {

    @ArchTest
    ArchRule noAnnotationDeprecated = noClasses()
            .should().beAnnotatedWith(Deprecated.class);

//    @ArchTest
//    ArchRule controllerShouldOnlyAccessApplicationService = classes()
//            .that().resideInAPackage("..controller..")
//            .should().onlyAccessClassesThat().resideInAPackage("..service..");

//    @ArchTest
//    ArchRule onlyKonstruktorInjection = noFields()
//		.should(beAnnotatedWith("org.springframework.beans.factory.annotation.Autowired"));

    @ArchTest
    ArchRule layerTest = layeredArchitecture()
            .consideringAllDependencies()
            .layer("controller").definedBy("..controller..")
            .layer("applicationService").definedBy("..service..")
            .layer("persistence").definedBy("..persistence..")
            .whereLayer("controller").mayNotBeAccessedByAnyLayer()
            .whereLayer("applicationService").mayOnlyAccessLayers("persistence")
            .whereLayer("applicationService").mayOnlyBeAccessedByLayers("controller")
            .whereLayer("persistence").mayOnlyBeAccessedByLayers("applicationService")
            .whereLayer("persistence").mayNotAccessAnyLayer();
}
