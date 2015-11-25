@Grapes( @Grab('org.ccil.cowan.tagsoup:tagsoup:1.2') )
@Grapes( @Grab('com.xlson.groovycsv:groovycsv:1.0') )
@Grapes( @Grab('com.cloudinary:cloudinary-http42:1.2.0') )
import com.xlson.groovycsv.*
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils

import org.ccil.cowan.tagsoup.Parser

import java.text.Normalizer
import java.util.regex.Pattern;

String ENCODING = "big5"

File csvFile = new File("products.csv")
def csv = csvFile.text
def parsed = new CsvParser().parse(csv, autoDetect: true)

Map headerMap = [
        layout: "product",
        bigTitle: "FIRE-RATED",
        smallTitle: "Door",
        description: "Gwapo si Ru",
        productCategoryName: "FIRE-RATED DOORS",
        coverBanner: "http://res.cloudinary.com/foxconn/image/upload/v1444980718/shutter/hero-img.jpg"
]
Map productMap = [:]
productMap.tier1 =[]

parsed.findAll().groupBy { it.category }.each { cat ->
    Map category = [:]
    category.name = cat.key
    category.slug = slugify(cat.key as String)
    category.products = []
    cat.value.eachWithIndex { prod, index ->
        println "Parsing ${prod.name} | ${slugify(prod.name as String)}"
        List patterns = parseURL(prod.url as String)
        category.products << [
                rank: index + 1,
                name: prod.name,
                slug: slugify(prod.name as String),
                patterns: patterns
        ]
    }
    productMap.tier1 << category
}

Map data = headerMap << productMap
exportToMarkdown(data)

void exportToMarkdown(Map data){
    'mkdir -p out'.execute()
    File file = new File("out/firerateddoors.md")
    file.write ('---')
    file << '\n'
    file << "layout: \"$data.layout\"\n"
    file << "bigTitle: \"$data.bigTitle\"\n"
    file << "smallTitle: \"$data.smallTitle\"\n"
    file << "description: \"$data.description\"\n"
    file << "productCategoryName: \"$data.productCategoryName\"\n"
    file << "coverBanner: \"$data.coverBanner\"\n"

    file << "tier1:\n"
    data.tier1.each{ tier1 ->
        file << "   - name: \"$tier1.name\"\n"
        file << "     slug: \"$tier1.slug\"\n"
        file << "     products:\n"
        tier1.products.each { product ->
            file << "       - rank: \"$product.rank\"\n"
            file << "         name: \"$product.name\"\n"
            file << "         slug: \"$product.slug\"\n"
            file << "         patterns:\n"
            product.patterns.each { patterns ->
                file << "           - thumbnail: \"$patterns.thumbnail\"\n"
                file << "             fullsize: \"$patterns.fullsize\"\n"
                file << "             productID: \"$patterns.productID\"\n"
            }
        }
    }

    file << '---' << '\n'
}

List parseURL(String url){
    String baseUrl = url.take(url.indexOf('/', url.indexOf('://')+3))
    String ENCODING = "big5"
    int page = 1
    def parser = new XmlSlurper(new Parser() )

    String finalURL = "${url}&Page=${page}"
    println "Scraping ${finalURL}..."

    List patterns = []
    new URL(finalURL).withReader (ENCODING) { reader ->
        def document = parser.parse(reader)
        document.'**'.findAll { it.@height == '150' }
                .collect { d -> d.'**'.find { it.name() == 'a' }?.@href }
                .findAll()
                .each { product ->
            String productURL = baseUrl + "/" + product.text()
            Map productInfo = getProductInfo(productURL)

            patterns << productInfo
        }
        page++
        finalURL = "${url}&Page=${page}"
        int lastPage = document.'**'.findAll {it.name() == "font"}.findAll { it.toString().findAll("^\\[[0-9]\\]") }.size() != 0 ? document.'**'.findAll {it.name() == "font"}.findAll { it.toString().findAll("^\\[[0-9]\\]") }.last().toString().replaceAll("\\[|\\]", "") as Integer : 1

        while(page <= lastPage){
            println "Scraping ${finalURL}.."
            new URL(finalURL).withReader (ENCODING) { reader2 ->
                def document2 = parser.parse(reader2)
                document2.'**'.findAll { it.@height == '150' }
                        .collect { d -> d.'**'.find { it.name() == 'a' }?.@href }
                        .findAll()
                        .each { product ->
                    String productURL = baseUrl + "/" + product.text()
                    Map productInfo = getProductInfo(productURL)

                    patterns << productInfo
                }
            }
            page++
            finalURL = "${url}&Page=${page}"
        }

        return patterns
    }


}

Map getProductInfo(String url){
    String baseUrl = url.take(url.indexOf('/', url.indexOf('://')+3))
    String ENCODING = "big5"
    def parser = new XmlSlurper(new Parser() )

    new URL(url).withReader (ENCODING) { reader ->
        def document = parser.parse(reader)

        String productID = document.'**'.findAll { it.@class.text() == 'p01' }.find { it ->  it.text().find(/[\d]{2}-[\d]{3}/)}.toString().find(/[\d]{2}-[\d]{3}/)
        println productID
        if(productID == "null"){
            println "aw"
        }
        String id = document.'**'.find { it.@target == 'pic' }*.@href.first().toString().split("/").last().find("[0-9]*").toString()
        String thumb = "$baseUrl/images_pdt/${id}_s.jpg"
        String full = "$baseUrl/images_pdt/${id}_1.jpg"
//            String thumbnailURL = getCloudinary().uploader().upload(thumb, ObjectUtils.asMap("public_id", "products/" + id + "_thumbnail")).url
        String thumbnailURL = "http://res.cloudinary.com/foxconn/image/upload/v1446478031/products/${id}_thumbnail.jpg"
        println "Uploaded ${thumbnailURL}"
//            String fullURL = getCloudinary().uploader().upload(full, ObjectUtils.asMap("public_id", "products/" + id + "_full")).url
        String fullURL = "http://res.cloudinary.com/foxconn/image/upload/v1446478031/products/${id}_full.jpg"
        println "Uploaded ${fullURL}"

        return [
                thumbnail: thumbnailURL,
                fullsize: fullURL,
                productID: productID
        ]
    }
}

Cloudinary getCloudinary(){
    cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "foxconn",
            "api_key", "key",
            "api_secret", "secret"))
    return cloudinary
}

String slugify (String string){
    Pattern NONLATIN = Pattern.compile("[^\\w-]")
    Pattern WHITESPACE = Pattern.compile("[\\s]")
    String nowhitespace = WHITESPACE.matcher(string).replaceAll("-")
    String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD)
    String slug = NONLATIN.matcher(normalized).replaceAll("")

    return slug.toLowerCase(Locale.ENGLISH)
}