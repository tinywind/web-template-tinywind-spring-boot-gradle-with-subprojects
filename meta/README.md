# pojo file 생성 스크립트
```
import com.intellij.database.model.DasTable
import com.intellij.database.util.Case
import com.intellij.database.util.DasUtil

packageName = "org.tinywind.server.model.entity;"
typeMapping = [
        (~/(?i)raw/)                      : "UUID",
        (~/(?i)int/)                      : "Long",
        (~/(?i)float\(1\)/)               : "Boolean",
        (~/(?i)float|double|decimal|real/): "Double",
        (~/(?i)datetime|timestamp/)       : "Timestamp",
        (~/(?i)date/)                     : "Date",
        (~/(?i)time/)                     : "Time",
        (~/(?i)/)                         : "String"
]
enumTypeMapping = [
        "user.grade"              : "UserGrade"
]

FILES.chooseDirectoryAndSave("Choose directory", "Choose where to store generated files") { dir ->
    SELECTION.filter { it instanceof DasTable }.each { generate(it, dir) }
}

def generate(table, dir) {
    def className = javaName(table.getName(), true) + "Entity"
    def fields = calcFields(table)
    new File(dir, "${className}.java").withPrintWriter { out -> generate(out, className, fields) }
}

def generate(out, className, fields) {
    out.println "package $packageName"
    out.println ""
    out.println "import lombok.Data;"
    out.println ""
    out.println "import java.sql.*;"
    out.println "import org.tinywind.server.model.enums.*;"
    out.println "import java.util.UUID;"
    out.println ""
    out.println "@Data"
    out.println "public class ${className} {"
    fields.each() {
        if (it.annos != "") out.println "  ${it.annos}"
        out.println "    private ${it.type} ${it.name};"
    }
    out.println "}"
}

def calcFields(table) {
    DasUtil.getColumns(table).reduce([]) { fields, col ->
        def enumTypeStr = enumTypeMapping["${table.getName()}.${col.getName()}"]
        def spec = Case.LOWER.apply(col.getDataType().getSpecification())
        def typeStr = typeMapping.find { p, t -> p.matcher(spec).find() }.value
        fields += [[name : javaName(col.getName(), false),
                    type : enumTypeStr ? enumTypeStr : typeStr,
                    annos: ""]]
    }
}

def javaName(str, capitalize) {
    def s = com.intellij.psi.codeStyle.NameUtil.splitNameIntoWords(str)
            .collect { Case.LOWER.apply(it).capitalize() }
            .join("")
            .replaceAll(/[^\p{javaJavaIdentifierPart}[_]]/, "_")
    capitalize || s.length() == 1 ? s : Case.LOWER.apply(s[0]) + s[1..-1]
}
```