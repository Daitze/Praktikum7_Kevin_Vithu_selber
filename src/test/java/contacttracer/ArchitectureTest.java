package contacttracer;

import com.sun.tools.javac.Main;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import contacttracer.stereotypes.AggregateRoot;
import contacttracer.stereotypes.ClassOnly;
import contacttracer.stereotypes.Wertobjekt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.conditions.ArchConditions.beAnnotatedWith;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packagesOf = ContacttracerApplication.class)
public class ArchitectureTest {

    @ArchTest
    ArchRule noAnnotationDeprecated = noClasses()
            .should().beAnnotatedWith(Deprecated.class);

    @ArchTest
    ArchRule controllerShouldOnlyAccessApplicationService = noClasses()
            .that().resideInAPackage("..controller..")
            .should().accessClassesThat().resideInAPackage("..persistence..");

//    @ArchTest
//    ArchRule onlyKonstruktorInjection = fields().should().notBeAnnotatedWith(Inject).andShould().beAnnotatedWith(Autowired.class)

    @ArchTest
    ArchRule layerTest = layeredArchitecture()
            .consideringAllDependencies()
            .layer("controller").definedBy("..controller..")
            .layer("applicationService").definedBy("..service..")
            .layer("persistence").definedBy("..persistence..")
            .layer("aggregate").definedBy("..aggregates.kontaktliste..")
            .whereLayer("controller").mayNotBeAccessedByAnyLayer()
            .whereLayer("applicationService").mayOnlyBeAccessedByLayers("controller")
            .whereLayer("persistence").mayOnlyBeAccessedByLayers("applicationService")
            .whereLayer("aggregate").mayOnlyBeAccessedByLayers("persistence","applicationService");
    @ArchTest
    ArchRule stereoType = classes().should().notBeAnnotatedWith(Configuration.class);
    @ArchTest
    ArchRule AggregateRootisAnnotated = fields()
            .that().areAnnotatedWith(Id.class)
            .should().beDeclaredInClassesThat().areAnnotatedWith(AggregateRoot.class)
            .orShould().beDeclaredInClassesThat().areAnnotatedWith(Wertobjekt.class);
    @ArchTest
    ArchRule AllConstructorArePublicifNotClassOnly = constructors().that().areAnnotatedWith(ClassOnly.class).should().bePrivate();
    @ArchTest
    ArchRule AllMethodsArePublicifNotClassOnly = methods().that().areAnnotatedWith(ClassOnly.class).should().bePrivate();
}
