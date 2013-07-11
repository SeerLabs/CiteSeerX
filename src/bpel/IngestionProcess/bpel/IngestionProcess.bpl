<?xml version="1.0" encoding="UTF-8"?>
<!--
BPEL Process Definition
IGC
-->
<bpel:process xmlns:bpel="http://docs.oasis-open.org/wsbpel/2.0/process/executable" #xmlns:ns="http://doi.citeseerx.psu.edu/xsd"# xmlns:ns1="http://citeseerx.org/ingestion/wsdl" xmlns:ns2="http://citeseerx.org/fileConversion/wsdl" xmlns:ns3="http://citeseerx.org/algorithms/docfilter/wsdl" xmlns:ns4="http://citeseerx.org/algorithms/svm-header-parse/wsdl" xmlns:ns5="http://citeseerx.org/algorithms/parscit/wsdl" #xmlns:ns6="http://doi.citeseerx.psu.edu"# xmlns:xsd="http://www.w3.org/2001/XMLSchema" name="IngestionProcess" suppressJoinFailure="yes" targetNamespace="http://citeseerx.org/IngestionProcess">
   <bpel:import importType="http://schemas.xmlsoap.org/wsdl/" location="wsdl/IngestionService.wsdl" namespace="http://citeseerx.org/ingestion/wsdl"/>
   <bpel:import importType="http://schemas.xmlsoap.org/wsdl/" location="wsdl/FileConversion.wsdl" namespace="http://citeseerx.org/fileConversion/wsdl"/>
   <bpel:import importType="http://schemas.xmlsoap.org/wsdl/" location="wsdl/DocFilter.wsdl" namespace="http://citeseerx.org/algorithms/docfilter/wsdl"/>
   <bpel:import importType="http://schemas.xmlsoap.org/wsdl/" location="wsdl" namespace="http://citeseerx.org/algorithms/svm-header-parse/wsdl"/>
   <bpel:import importType="http://schemas.xmlsoap.org/wsdl/" location="wsdl/ParsCit.wsdl" namespace="http://citeseerx.org/algorithms/parscit/wsdl"/>
#   <bpel:import importType="http://schemas.xmlsoap.org/wsdl/" location="file:wsdl/DOIService.wsdl" namespace="http://doi.citeseerx.psu.edu"/>
   <bpel:partnerLinks>
      <bpel:partnerLink myRole="Ingester" name="ingestionPartnerLT" partnerLinkType="ns1:ingestionPartnerLT"/>
      <bpel:partnerLink name="conversionPartnerLT" partnerLinkType="ns2:conversionPartnerLT" partnerRole="Converter"/>
      <bpel:partnerLink name="filterPartnerLT" partnerLinkType="ns3:filterPartnerLT" partnerRole="Filterer"/>
      <bpel:partnerLink name="parsePartnerLT" partnerLinkType="ns4:parsePartnerLT" partnerRole="Parser"/>
      <bpel:partnerLink name="parsePartnerLT1" partnerLinkType="ns5:parsePartnerLT" partnerRole="Parser"/>
#      <bpel:partnerLink name="doiPartnerLT" partnerLinkType="ns6:doiPartnerLT" partnerRole="Identifier"/>
   </bpel:partnerLinks>
   <bpel:variables>
      <bpel:variable messageType="ns1:IngestionRequest" name="IngestionRequest"/>
      <bpel:variable messageType="ns1:IngestionResponse" name="IngestionResponse"/>
      <bpel:variable messageType="ns2:extractTextRequest" name="extractTextRequest"/>
      <bpel:variable messageType="ns2:extractTextResponse" name="extractTextResponse"/>
      <bpel:variable messageType="ns3:filterRequest" name="filterRequest"/>
      <bpel:variable messageType="ns3:filterResponse" name="filterResponse"/>
      <bpel:variable messageType="ns4:parseHeaderRequest" name="parseHeaderRequest"/>
      <bpel:variable messageType="ns4:parseHeaderResponse" name="parseHeaderResponse"/>
      <bpel:variable messageType="ns5:extractCitationsRequest" name="extractCitationsRequest"/>
      <bpel:variable messageType="ns5:extractCitationsResponse" name="extractCitationsResponse"/>
#      <bpel:variable messageType="ns6:getDOIMessage" name="getDOIMessage"/>
#      <bpel:variable messageType="ns6:getDOIResponse" name="getDOIResponse"/>
      <bpel:variable messageType="ns1:IngestionFault" name="IngestionError"/>
      <bpel:variable messageType="ns3:DocFilterFault" name="DocFilterError"/>
   </bpel:variables>
   <bpel:faultHandlers>
      <!-- DocFilterFault with variable -->
      <bpel:catch faultMessageType="ns3:DocFilterFault" faultName="ns3:DocFilterFault" faultVariable="filterError">
         <bpel:flow>
            <bpel:links>
               <bpel:link name="assign-filterFaultVar-to-reply"/>
            </bpel:links>
            <bpel:reply faultName="ns1:IngestionFault" operation="ingest" partnerLink="ingestionPartnerLT" portType="ns1:IngestionPT" variable="IngestionError">
               <bpel:targets>
                  <bpel:target linkName="assign-filterFaultVar-to-reply"/>
               </bpel:targets>
            </bpel:reply>
            <bpel:assign>
               <bpel:sources>
                  <bpel:source linkName="assign-filterFaultVar-to-reply"/>
               </bpel:sources>
               <bpel:copy>
                  <bpel:from>
                     <bpel:literal>Document failed filtration - does not appear to be a research paper</bpel:literal>
                  </bpel:from>
                  <bpel:to part="message" variable="IngestionError"/>
               </bpel:copy>
            </bpel:assign>
         </bpel:flow>
      </bpel:catch>
      <!-- DocFilterFault (no fault variable) -->
      <bpel:catch faultName="ns3:DocFilterFault">
         <bpel:flow>
            <bpel:links>
               <bpel:link name="assign-docFilterFault-to-reply"/>
            </bpel:links>
            <bpel:assign>
               <bpel:sources>
                  <bpel:source linkName="assign-docFilterFault-to-reply"/>
               </bpel:sources>
               <bpel:copy>
                  <bpel:from>
                     <bpel:literal>Document failed filtration due to a system error</bpel:literal>
                  </bpel:from>
                  <bpel:to part="message" variable="IngestionError"/>
               </bpel:copy>
            </bpel:assign>
            <bpel:reply faultName="ns1:IngestionFault" operation="ingest" partnerLink="ingestionPartnerLT" portType="ns1:IngestionPT" variable="IngestionError">
               <bpel:targets>
                  <bpel:target linkName="assign-docFilterFault-to-reply"/>
               </bpel:targets>
            </bpel:reply>
         </bpel:flow>
      </bpel:catch>
      <!-- FileConversionFault -->
      <bpel:catch faultName="ns2:FileConversionFault">
         <bpel:flow>
            <bpel:links>
               <bpel:link name="assign-conversionFault-to-reply"/>
            </bpel:links>
            <bpel:assign>
               <bpel:sources>
                  <bpel:source linkName="assign-conversionFault-to-reply"/>
               </bpel:sources>
               <bpel:copy>
                  <bpel:from>
                     <bpel:literal>Document could not be properly converted to text</bpel:literal>
                  </bpel:from>
                  <bpel:to part="message" variable="IngestionError"/>
               </bpel:copy>
            </bpel:assign>
            <bpel:reply faultName="ns1:IngestionFault" operation="ingest" partnerLink="ingestionPartnerLT" portType="ns1:IngestionPT" variable="IngestionError">
               <bpel:targets>
                  <bpel:target linkName="assign-conversionFault-to-reply"/>
               </bpel:targets>
            </bpel:reply>
         </bpel:flow>
      </bpel:catch>
      <!-- SVMHeaderFault -->
      <bpel:catch faultName="ns4:SVMHeaderParseFault">
         <bpel:flow>
            <bpel:links>
               <bpel:link name="assign-headerFault-to-reply"/>
            </bpel:links>
            <bpel:assign>
               <bpel:sources>
                  <bpel:source linkName="assign-headerFault-to-reply"/>
               </bpel:sources>
               <bpel:copy>
                  <bpel:from>
                     <bpel:literal>A fatal error occurred during header extraction</bpel:literal>
                  </bpel:from>
                  <bpel:to part="message" variable="IngestionError"/>
               </bpel:copy>
            </bpel:assign>
            <bpel:reply faultName="ns1:IngestionFault" operation="ingest" partnerLink="ingestionPartnerLT" portType="ns1:IngestionPT" variable="IngestionError">
               <bpel:targets>
                  <bpel:target linkName="assign-headerFault-to-reply"/>
               </bpel:targets>
            </bpel:reply>
         </bpel:flow>
      </bpel:catch>
      <!-- ParsCitFault -->
      <bpel:catch faultName="ns5:ParsCitFault">
         <bpel:flow>
            <bpel:links>
               <bpel:link name="assign-parsCitFault-to-reply"/>
            </bpel:links>
            <bpel:assign>
               <bpel:sources>
                  <bpel:source linkName="assign-parsCitFault-to-reply"/>
               </bpel:sources>
               <bpel:copy>
                  <bpel:from>
                     <bpel:literal>A fatal error occurred during citation extraction</bpel:literal>
                  </bpel:from>
                  <bpel:to part="message" variable="IngestionError"/>
               </bpel:copy>
            </bpel:assign>
            <bpel:reply faultName="ns1:IngestionFault" operation="ingest" partnerLink="ingestionPartnerLT" portType="ns1:IngestionPT" variable="IngestionError">
               <bpel:targets>
                  <bpel:target linkName="assign-parsCitFault-to-reply"/>
               </bpel:targets>
            </bpel:reply>
         </bpel:flow>
      </bpel:catch>
#      <!-- DOIFault -->
#      <bpel:catch faultName="ns6:getDOIFault">
#         <bpel:flow>
#            <bpel:links>
#               <bpel:link name="assign-doiFault-to-reply"/>
#            </bpel:links>
#            <bpel:assign>
#               <bpel:sources>
#                  <bpel:source linkName="assign-doiFault-to-reply"/>
#               </bpel:sources>
#               <bpel:copy>
#                  <bpel:from>
#                     <bpel:literal>A fatal error occured when retrieving a DOI for this document</bpel:literal>
#                  </bpel:from>
#                  <bpel:to part="message" variable="IngestionError"/>
#               </bpel:copy>
#            </bpel:assign>
#            <bpel:reply faultName="ns1:IngestionFault" operation="ingest" partnerLink="ingestionPartnerLT" portType="ns1:IngestionPT" variable="IngestionError">
#               <bpel:targets>
#                  <bpel:target linkName="assign-doiFault-to-reply"/>
#               </bpel:targets>
#            </bpel:reply>
#         </bpel:flow>
#      </bpel:catch>
      <bpel:catchAll>
         <bpel:flow>
            <bpel:links>
               <bpel:link name="assign-unknownFault-to-reply"/>
            </bpel:links>
            <bpel:assign>
               <bpel:sources>
                  <bpel:source linkName="assign-unknownFault-to-reply"/>
               </bpel:sources>
               <bpel:copy>
                  <bpel:from>
                     <bpel:literal>An unknown fault occurred during the ingestion process</bpel:literal>
                  </bpel:from>
                  <bpel:to part="message" variable="IngestionError"/>
               </bpel:copy>
            </bpel:assign>
            <bpel:reply faultName="ns1:IngestionFault" operation="ingest" partnerLink="ingestionPartnerLT" portType="ns1:IngestionPT" variable="IngestionError">
               <bpel:targets>
                  <bpel:target linkName="assign-unknownFault-to-reply"/>
               </bpel:targets>
            </bpel:reply>
         </bpel:flow>
      </bpel:catchAll>
   </bpel:faultHandlers>
   <bpel:flow>
      <bpel:links>
         <bpel:link name="textExtraction-to-assign"/>
#         <bpel:link name="headerParse-to-assign-DOI"/>
         <bpel:link name="headerParse-to-assign"/>
#         <bpel:link name="parscit-to-assign-DOI"/>
         <bpel:link name="parscit-to-assign"/>
#         <bpel:link name="getDOI-to-assign-DOI"/>
         <bpel:link name="assign-to-textExtraction"/>
         <bpel:link name="docFilter-to-headerParse"/>
         <bpel:link name="docFilter-to-parscit"/>
         <bpel:link name="docFilter-to-assignError"/>
         <bpel:link name="assign-to-docFilter"/>
#         <bpel:link name="assign-DOI-to-getDOI"/>
         <bpel:link name="assign-header-to-reply"/>
#         <bpel:link name="assign-DOI-to-reply"/>
         <bpel:link name="assign-parscit-to-reply"/>
         <bpel:link name="receive-ingest-req-to-assign"/>
         <bpel:link name="assign-filterError-to-throwFault"/>
      </bpel:links>
      <bpel:invoke inputVariable="extractTextRequest" name="InvokeTextExtraction" operation="extractText" outputVariable="extractTextResponse" partnerLink="conversionPartnerLT" portType="ns2:FileConversionPT">
         <bpel:targets>
            <bpel:target linkName="assign-to-textExtraction"/>
         </bpel:targets>
         <bpel:sources>
            <bpel:source linkName="textExtraction-to-assign"/>
         </bpel:sources>
      </bpel:invoke>
      <bpel:invoke inputVariable="parseHeaderRequest" name="InvokeSVMHeaderParse" operation="parseHeader" outputVariable="parseHeaderResponse" partnerLink="parsePartnerLT" portType="ns4:SVMHeaderParsePT">
         <bpel:targets>
            <bpel:target linkName="docFilter-to-headerParse"/>
         </bpel:targets>
         <bpel:sources>
#            <bpel:source linkName="headerParse-to-assign-DOI"/>
            <bpel:source linkName="headerParse-to-assign"/>
         </bpel:sources>
      </bpel:invoke>
      <bpel:invoke inputVariable="extractCitationsRequest" name="InvokeParscit" operation="extractCitations" outputVariable="extractCitationsResponse" partnerLink="parsePartnerLT1" portType="ns5:ParsCitPT">
         <bpel:targets>
            <bpel:target linkName="docFilter-to-parscit"/>
         </bpel:targets>
         <bpel:sources>
#            <bpel:source linkName="parscit-to-assign-DOI"/>
            <bpel:source linkName="parscit-to-assign"/>
         </bpel:sources>
      </bpel:invoke>
#      <bpel:invoke inputVariable="getDOIMessage" name="InvokeGetDOI" operation="getDOI" outputVariable="getDOIResponse" partnerLink="doiPartnerLT" portType="ns6:DOIServerPortType">
#         <bpel:targets>
#            <bpel:target linkName="assign-DOI-to-getDOI"/>
#         </bpel:targets>
#         <bpel:sources>
#            <bpel:source linkName="getDOI-to-assign-DOI"/>
#         </bpel:sources>
#      </bpel:invoke>
      <bpel:assign name="Assign-extractionMessage-and-repIDs">
         <bpel:targets>
            <bpel:target linkName="receive-ingest-req-to-assign"/>
         </bpel:targets>
         <bpel:sources>
            <bpel:source linkName="assign-to-textExtraction"/>
         </bpel:sources>
         <bpel:copy>
            <bpel:from part="fileID" variable="IngestionRequest"/>
            <bpel:to part="filePath" variable="extractTextRequest"/>
         </bpel:copy>
         <bpel:copy>
            <bpel:from part="repositoryID" variable="IngestionRequest"/>
            <bpel:to part="repositoryID" variable="extractTextRequest"/>
         </bpel:copy>
         <bpel:copy>
            <bpel:from part="repositoryID" variable="IngestionRequest"/>
            <bpel:to part="repositoryID" variable="filterRequest"/>
         </bpel:copy>
         <bpel:copy>
            <bpel:from part="repositoryID" variable="IngestionRequest"/>
            <bpel:to part="repositoryID" variable="parseHeaderRequest"/>
         </bpel:copy>
         <bpel:copy>
            <bpel:from part="repositoryID" variable="IngestionRequest"/>
            <bpel:to part="repositoryID" variable="extractCitationsRequest"/>
         </bpel:copy>
      </bpel:assign>
      <bpel:invoke inputVariable="filterRequest" name="InvokeDocFilter" operation="filter" outputVariable="filterResponse" partnerLink="filterPartnerLT" portType="ns3:DocFilterPT">
         <bpel:targets>
            <bpel:target linkName="assign-to-docFilter"/>
         </bpel:targets>
         <bpel:sources>
            <bpel:source linkName="docFilter-to-headerParse">
               <bpel:transitionCondition>$filterResponse.status &gt;0</bpel:transitionCondition>
            </bpel:source>
            <bpel:source linkName="docFilter-to-parscit">
               <bpel:transitionCondition>$filterResponse.status &gt;0</bpel:transitionCondition>
            </bpel:source>
            <bpel:source linkName="docFilter-to-assignError">
               <bpel:transitionCondition>$filterResponse.status &lt;=0</bpel:transitionCondition>
            </bpel:source>
         </bpel:sources>
      </bpel:invoke>
      <bpel:assign name="Assign-textPath-and-conversionTrace">
         <bpel:targets>
            <bpel:target linkName="textExtraction-to-assign"/>
         </bpel:targets>
         <bpel:sources>
            <bpel:source linkName="assign-to-docFilter"/>
         </bpel:sources>
         <bpel:copy>
            <bpel:from part="filePath" variable="extractTextResponse"/>
            <bpel:to part="fileID" variable="filterRequest"/>
         </bpel:copy>
         <bpel:copy>
            <bpel:from part="filePath" variable="extractTextResponse"/>
            <bpel:to part="filePath" variable="parseHeaderRequest"/>
         </bpel:copy>
         <bpel:copy>
            <bpel:from part="filePath" variable="extractTextResponse"/>
            <bpel:to part="filePath" variable="extractCitationsRequest"/>
         </bpel:copy>
         <bpel:copy>
            <bpel:from part="conversionTrace" variable="extractTextResponse"/>
            <bpel:to part="ConversionTrace" variable="IngestionResponse"/>
         </bpel:copy>
      </bpel:assign>
#      <bpel:assign name="Assign-DOI-request">
#         <bpel:targets>
#            <bpel:target linkName="headerParse-to-assign-DOI"/>
#            <bpel:target linkName="parscit-to-assign-DOI"/>
#         </bpel:targets>
#         <bpel:sources>
#            <bpel:source linkName="assign-DOI-to-getDOI"/>
#         </bpel:sources>
#         <bpel:copy>
#            <bpel:from>
#               <bpel:literal>
#                  <ns:getDOI>
#                     <ns:doiType>1</ns:doiType>
#                  </ns:getDOI>
#               </bpel:literal>
#            </bpel:from>
#            <bpel:to part="part1" variable="getDOIMessage"/>
#         </bpel:copy>
#      </bpel:assign>
      <bpel:assign name="Assign-header-to-reply">
         <bpel:targets>
            <bpel:target linkName="headerParse-to-assign"/>
         </bpel:targets>
         <bpel:sources>
            <bpel:source linkName="assign-header-to-reply"/>
         </bpel:sources>
         <bpel:copy>
            <bpel:from part="headerInfo" variable="parseHeaderResponse"/>
            <bpel:to part="SVMHeaderResponse" variable="IngestionResponse"/>
         </bpel:copy>
      </bpel:assign>
#      <bpel:assign name="Assign-doi-to-reply">
#         <bpel:targets>
#            <bpel:target linkName="getDOI-to-assign-DOI"/>
#         </bpel:targets>
#         <bpel:sources>
#            <bpel:source linkName="assign-DOI-to-reply"/>
#         </bpel:sources>
#         <bpel:copy>
#            <bpel:from part="part1" variable="getDOIResponse">
#               <bpel:query>ns:return</bpel:query>
#            </bpel:from>
#            <bpel:to part="DOI" variable="IngestionResponse"/>
#         </bpel:copy>
#      </bpel:assign>
      <bpel:assign name="Assign-parscit-to-reply">
         <bpel:targets>
            <bpel:target linkName="parscit-to-assign"/>
         </bpel:targets>
         <bpel:sources>
            <bpel:source linkName="assign-parscit-to-reply"/>
         </bpel:sources>
         <bpel:copy>
            <bpel:from part="citations" variable="extractCitationsResponse"/>
            <bpel:to part="ParsCitResponse" variable="IngestionResponse"/>
         </bpel:copy>
         <bpel:copy>
            <bpel:from part="citeFile" variable="extractCitationsResponse"/>
            <bpel:to part="CiteFile" variable="IngestionResponse"/>
         </bpel:copy>
         <bpel:copy>
            <bpel:from part="bodyFile" variable="extractCitationsResponse"/>
            <bpel:to part="BodyFile" variable="IngestionResponse"/>
         </bpel:copy>
      </bpel:assign>
      <bpel:receive createInstance="yes" name="ReceiveIngestionRequest" operation="ingest" partnerLink="ingestionPartnerLT" portType="ns1:IngestionPT" variable="IngestionRequest">
         <bpel:sources>
            <bpel:source linkName="receive-ingest-req-to-assign"/>
         </bpel:sources>
      </bpel:receive>
      <bpel:reply name="ReplyToClient" operation="ingest" partnerLink="ingestionPartnerLT" portType="ns1:IngestionPT" variable="IngestionResponse">
         <bpel:targets>
#            <bpel:target linkName="assign-DOI-to-reply"/>
            <bpel:target linkName="assign-parscit-to-reply"/>
            <bpel:target linkName="assign-header-to-reply"/>
         </bpel:targets>
      </bpel:reply>
      <bpel:throw faultName="ns3:DocFilterFault" faultVariable="DocFilterError" name="ThrowFilterFault">
         <bpel:targets>
            <bpel:target linkName="assign-filterError-to-throwFault"/>
         </bpel:targets>
      </bpel:throw>
      <bpel:assign name="Assign-filterError">
         <bpel:targets>
            <bpel:target linkName="docFilter-to-assignError"/>
         </bpel:targets>
         <bpel:sources>
            <bpel:source linkName="assign-filterError-to-throwFault"/>
         </bpel:sources>
         <bpel:copy>
            <bpel:from part="msg" variable="filterResponse"/>
            <bpel:to part="message" variable="DocFilterError"/>
         </bpel:copy>
      </bpel:assign>
   </bpel:flow>
</bpel:process>
