import java.io.*;
import org.broad.tribble.AbstractFeatureReader;
import org.broad.tribble.FeatureReader;
import org.broadinstitute.sting.utils.Utils;
import org.broadinstitute.variant.vcf.VCFCodec;
import org.broadinstitute.variant.vcf.VCFHeader;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.broadinstitute.variant.variantcontext.writer.*;
import net.sf.samtools.SAMSequenceDictionary;
import net.sf.picard.reference.*;

import java.util.Iterator;
import java.util.Map;
/**
 * motivation:
 *      copy a VCF 
 * usage:
 * javac -cp ${GATK}  ReadVCF.java
 * java -cp ${GATK}:. ReadVCF ref.fa my.vcf
 */
public class VCFReader
{
 public static void main(String args[]) throws Exception
  {
  /** latest VCF specification */
  final VCFCodec vcfCodec = new VCFCodec();
  /** we don't need some indexed VCFs */
  boolean requireIndex=false;
  /* load a SAM sequence dictionary */
  SAMSequenceDictionary dict=new IndexedFastaSequenceFile(
    new File(args[0])).getSequenceDictionary();
  /* loop over each vcf */
  for(int i=1;i< args.length;++i)
   {
   /* input VCF */
   String filename=args[i];
   /* output VCF */
   File fileout=new File("tmp"+i+".vcf"); 
   VariantContextWriter writer=VariantContextWriterFactory.create(fileout,dict);
   /* get A VCF Reader */
   FeatureReader<VariantContext> reader = AbstractFeatureReader.getFeatureReader(
      filename, vcfCodec, requireIndex);
   /* read the header */
   VCFHeader header = (VCFHeader)reader.getHeader();
   /* write the header */
   writer.writeHeader(header);
   /** loop over each Variation */
   Iterator<VariantContext> it = reader.iterator();
              while ( it.hasNext() )
               {
               /* get next variation and save it */
     VariantContext vc = it.next();
     writer.add(vc);
    }
   /* we're done */
   reader.close();
   writer.close();
   }  
  }
 }