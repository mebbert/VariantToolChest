### Introduction






### Set Operators





### VarStat (vs)

This portion of the program will output the the variant statistical report as a summary or the association test or both.

Sample inputs are as follow:

<pre><code>
>vs -i input1.vcf -s
# or
>vs -i input1.vcf input2.vcf -s -c
# or
>vs -i input1.vcf input2.vcf -s
# or
>vs -i input1.vcf -a -p pheno1.txt -s
# or
>vs -i input1.vcf -a -p pheno1.txt 
</code></pre>


### Statistical Summary (-s)

To run the summary multiple input file are allowed. The default print function will print one summary per file.
The -c parameter will print a combined summary with all the files' names printed at the top.  The input files may only
be .vcf files.

**Sample terminal input**
<pre><code>
>vs -i input1.vcf -s
# or
>vs -i input1.vcf input2.vcf -s -c
# or
>vs -i input1.vcf input2.vcf -s
</code></pre>

The first option will output the summary for that file.  
The second will output the combined summary of the two files.
The third will output individual summaries for each of the files.

The Summary outputs the following statistics to the screen.
	
<pre><code>
=================================
                              
 Summary of v1: input.vcf         
                              
=================================

+-------------------------------+
|TotalVars:                 195 |
+-------------------------------+
|    SNVs:                    0 |
|         Ti/Tv:            NaN |
|   (Geno)Ti/Tv:            NaN |
+-------------------------------+
|    INDELs:                195 |
+-------------------------------+
|    StructVars:              0 |
+-------------------------------+
|MultiAlts:                   0 |
+-------------------------------+
|AvgQualScore:              NaN |
|  MinQualScore:            NaN |
|  MaxQualScore:            NaN |
+-------------------------------+
|AvgDepth:                  NaN |
|      MinDepth:            NaN |
|      MaxDepth:            NaN |
+-------------------------------+

There was an error in the Qual formatting of: 11.vcf  One or more variants had no Quality Score. It was excluded in the calculation.
There was an error in the Depth formatting of: 11.vcf  One or more variants had no Read Depth. It was excluded in the calculation.
</code></pre>
	
* **TotalVars** counts the total number of variants in the file/files.
* **SNVs** counts the number of Single Nucleotide Variants (SNVs) in the file/files.
* **Ti/Tv** outputs the ratio of Transition versus Transition SNVs. 
* **(Geno)Ti/Tv** outputs the genotypic ratio of Transition versus Tranversion SNVs.
* **INDELs** counts the number of Insertions & Deletions.
* **StructVars** counts the number of Structural Variants in the file.
* **MultiAlts** counts the number of variants that have multiple alternate alleles.
* **AvgQualScore** outputs the average quality score across all the variants in the file.
* **MinQualScore** outputs the lowest quality score found in the file.
* **MaxQualScore** outputs the highest quality score found in the file.
* **AvgDepth** outputs the average read depth across all the variants in the file.
* **MinDepth** outputs the lowest read depth for a variant found in the file.
* **MaxDepth** outputs the highest read depth for a variant found in the file.
If there is a "NaN" for either Ti/Tv or (Geno)Ti/Tv it means that there is division by zero.
The NaN for the quality scores and depth means that there were no Quality scores or read Depths, respectively, recorded. 
	In this case the error output simply means that there were missing Quality and/or Read Depths in the file.
  
### Association Test

This test calculates the p-value from the chi square test from the case-control allelic counts. 

The following are possible inputs:
<pre><code>
>vs -i input1.vcf -a -p pheno1.txt -s
# or
>vs -i input1.vcf -a -p pheno1.txt 
</code></pre>

The phenotype file needs to be a tab delimited file formatted as follows:
<pre><code>
HG00127	1
HG00128	2
HG00136	2
HG00137	1
...
</code></pre>
The first column is the sample ID, and the second column is the case (2) control (1) status. All of the sample ID's that are in common
between the Pheno file and the VCF file will be analyzed in the association test.  There may be Samples that are not in one or the other, 
but these will not be included in the analysis and will not throw an error.

The output file has the following format:
<pre><code>
Chr	ID	Pos	Ref	Alt	CaseRefCount	CaseAltCount	ControlRefCount	ControlAltCount	OR	P-Value
20	.	669442	TG	T	389	33	323	21	0.7664	0.3563
20	.	719486	C	CT	420	0	339	1	NA	0.2661
20	.	890696	C	CAT	419	3	341	3	1.229	0.8013
20	.	1102516	CT	C	419	1	338	2	2.479	0.4440
20	.	1149576	CT	C	420	2	342	0	NA	0.2024
20	.	1195706	AAG	A	231	191	199	143	0.8691	0.3394
...
</code></pre>

The columns are as follows:
	* **Chr** is the chromosome number.
	* **ID** is the variant ID (i.e. rs2228467).
	* **Pos** is the position of the variant on the chromosome.
	* **Ref** is the reference allele.
	* **Alt** is the alternate allele. If there are more than one it is printed on another line.
	* **CaseRefCount** is the count of of how many reference alleles are present in the case samples.
	* **CaseAltCount** is the count of of how many alternate alleles are present in the case samples.
	* **ControlRefCount** is the count of of how many reference alleles are present in the control samples.
	* **ControlAltCount** is the count of of how many alternate alleles are present in the control samples.
	* **OR** is the odds ratio calculated from the case and control allele counts.
	* **P-Value** is the p-value calculated from the chi square test using the case and control allele counts.


