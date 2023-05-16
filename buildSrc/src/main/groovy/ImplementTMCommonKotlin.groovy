import org.gradle.api.Project

class ImplementTMCommonKotlin {
    /**
     * Allegedly, Groovy does not let you pass project directly, it must be within a closure.
     *
     * The method starts with "get" to avoid using extra parenthesis.
     */
    static Closure getInvoke() {
        return (Project project) -> {
            getInvokeInternal(project)
        }
    }

    private static void getInvokeInternal(Project project) {
        List<String> list = new ArrayList()
        list.add("tmcommonkotlin-androidx")
        list.add("tmcommonkotlin-core")
        list.add("tmcommonkotlin-coroutines")
        list.add("tmcommonkotlin-customviews")
        list.add("tmcommonkotlin-rx3")
        list.add("tmcommonkotlin-misc")
        list.add("tmcommonkotlin-tuple")
        list.add("tmcommonkotlin-view")
        list.add("tmcommonkotlin-imagetotext")
        list.forEach((s) -> {
            implementationAndSubstitute(project, s)
        })
        testImplementationAndSubstitute(project, "tmcommonkotlin-testoverridelog")
        testImplementationAndSubstitute(project, "tmcommonkotlin-test")
    }

    private static void implementationAndSubstitute(Project project, String s) {
        project.configurations.all {
            resolutionStrategy.dependencySubstitution {
                substitute module("com.github.Troy1010:${s}:${Constants.tmcommonkotlin}") using module("com.github.Troy1010.TMCommonKotlin:${s}:${Constants.tmcommonkotlin}")
            }
        }
        project.dependencies {
            if (project.property("useMavenLocal") == "true") {
                implementation "com.tminus1010.tmcommonkotlin:${s}:+"
            } else {
                implementation "com.github.Troy1010.TMCommonKotlin:${s}:${Constants.tmcommonkotlin}"
            }
        }
    }

    private static void testImplementationAndSubstitute(Project project, String s) {
        project.configurations.all {
            resolutionStrategy.dependencySubstitution {
                substitute module("com.github.Troy1010:${s}:${Constants.tmcommonkotlin}") using module("com.github.Troy1010.TMCommonKotlin:${s}:${Constants.tmcommonkotlin}")
            }
        }
        project.dependencies {
            if (project.property("useMavenLocal") == "true") {
                testImplementation "com.tminus1010.tmcommonkotlin:${s}:+"
            } else {
                testImplementation "com.github.Troy1010.TMCommonKotlin:${s}:${Constants.tmcommonkotlin}"
            }
        }
    }
}