# EventSpotter - spotting entities that talk about events

[EventSpotter][EventSpotter] is the event entity identification and disambiguation system created by the Multimedia Communication Department at [Institut Eurecom]. It identifies occurences of named musical event entities in unstructured text. Most event names are ambiguous, and Eventspotter takes into consideration this ambiguity. 

If you want to be notified about  news or new releases, send us a mail to:

	Amar.Kadamalakunte@eurecom.fr
	Meghana.Sreekanta-Murthy@eurecom.fr
    Raphael.Troncy@eurecom.fr
	Giuseppe.Rizzo@eurecom.fr

## Introduction to EventSpotter

EventSpotter is currently a framework for musical event detection and disambiguation. Given free form text, it maps occurences of musical event titles onto musical entities registered in the EventMedia dataset. EventSpotter is useful for extracting knowledge about the musical entities, such as artist, date, location, category as well.
EventMedia is a huge hub of social event data obtained from three public event directories: Last.fm, Eventful and Upcoming and the media directory: Flickr. We use a snapshot of the EventMedia dataset, available at[https://github.com/amark-india/eventspotter/tree/master/data/EventMedia_2012.csv].
EventTitles.list file can be considered an event indexer or lexicon which contains, as the name suggests, titles of all events contained in EventMedia mapped to their respective event id.

## Requirements

EventSpotter was written in Java, and requires Java 6. EventSpotter also needs a MySQL[https://github.com/amark-india/eventspotter/tree/master/data/eventspotterdb.sql] database to run.
EventSpotter uses a preprocessed snapshot of the EventMedia dataset found at [EventMedia_2012preprocessed.csv]. 
The machine that EventSpotter runs on should have a reasonable amount of main memory since we load the eventTitles.list file onto the memory when eventspotter file is executed.
Since the snapshot of the EventMedia dataset we used contains more than 65K entries, it is important to have sufficient main memory for smooth execution.

## Setting up the Eventspotterdb repository

EventSpotter was developed to disambiguate event entities using the EventMedia knowledge base, returning the EventMedia identifier for disambiguated entities. 
However, you can use EventSpotter with any entity repository, given that you have aritists and event descriptions for all entities. The more common case is to use EventSpotter with EventMedia. 
If you want to set it up with your own repository, see the Advanced Configuration section.

To use EventSpotter with EventMedia database , download the  EventMedia repository we provide on our [github repository] as a csv dump and import it into your database server or you can use the database file .
Once the import is done, you can start using EventSpotter immediately by adjusting the `/src/fr/eurecom/eventspotter/EventSpotter.java` file at line 36 to `this.dbPath = "jdbc:mysql://localhost/YourDataBaseName;` to point to the database.
EventSpotter will then use nearly 65K named event entities harvested from Last.fm, Upcoming and Eventful for disambiguation.

Get the Entity Repository (25.7 MB):

		https://github.com/amark-india/eventspotter/archive/master.zip

Unzip the eventspotter-master.zip file and find the database file @ /eventspotter-master/data/eventspotterdb.sql
		
Import it into a database:

		mysql -u user --password=password <DATABASE> < eventspotterdb.sql

    
where <DATABASE> is a database on a MySQL server.

## Setting up EventSpotter

To build EventSpotter, unzip eventspotter-master.zip and import the eventspotter-master into eclipse as an existing java project.
Add the libraries in the lib folder to the build path and export Main.java into a jar file. Refer to this for jar file creation in eclipse [http://help.eclipse.org/juno/index.jsp?topic=%2Forg.eclipse.jdt.doc.user%2Ftasks%2Ftasks-33.htm]
This will create an EventSpotter.jar including all dependencies.

## Eventspotter Hands-On

The main classes in EventSpotter are :

=>`fr.eurecom.eventspotter.EventSpotter` for creating the database connections, setting path variables, initiating the spotting of events, annotating the events found in the input file and finally presenting the output in a Json format. 


=>`fr.eurecom.eventspotter.worker.EventSpotterLight` for preparing the input document and running the disambiguation on the prepared input.

Input : unstructured text 
Output : disambiguated musical events

### Start the preprocessing by tokenizing input text
 tokens = tokenize(input)

 sentences = sentensize(input)

###Candidate Selection
 candidates = doSpotting(input, tokens, entries)
 entries is a list of tokens obtained by tokenizing the list of event titles file
 candidates are case insensitive matches of tokens with entries

###Disambiguation
for each candidate in candidates:

	if candidate doesn''t start with capital letter and first char is not a digit
          then continue to next candidate
	end if

    List<String> agents = getagents(eventId)
	Set<FeatureStructure> foundAgentToks = doSpotting(input, tokens, agents)	
	
	if atleast one agent is found in the input text  
		then get surroundings to compute confidence score
			
		Surround = getSurrounding(sentences,candidate);
		#If candidate is in the first line of input text, current and next line is surrounding.
		#If candidate is in the middle of input text, previous, current and next line is surrounding.
		#If candidate is in the last line of input text, current and previous line is surrounding.
	
		confidence = cosine similarity.run(Surround,event description)
	 
		confirm candidate as an event and add to FeatureStructure.
	endif
	
endfor
###Post processing
	Add `<EVENT>` tags to input text 
	Display annotated input text 
	Display spotted events with confidence score in JSON format
	


## Hands-On Command Line Call Example

1. Build EventSpotter:

    `ant`
    
1. Run the CommandLineDisambiguator:

    `java -Xmx4G -cp EventSpotter.jar -I <INPUT-FILE> -O <OUTPUT-FILE> `

`<INPUT-FILE>` is path to the text file to be annotated with entities. The format for `<INPUT-FILE>` should be plain text with UTF-8 encoding.
`<OUTPUT-FILE>` is path to the text file which will contain all the event entity spots in JSON format along with a confidence score for each spot.
Adding the -E <minus eval> option to generate a conll file for evaluation. 

The output will be displayed on the console. The EventSpotter displays an in-place replacement of event entities with  `<EVENT>`  entity  `</EVENT>` tags.
It also displays the details of each spot in JSON format along with the [http://data.linkedevents.org/] URL for each event. This JSON output is also written into the output file .

### Input Format

The input of EventSpotter is an English language text (as Java String) or file in UTF-8 encoding. 

## Advanced Configuration

### Using EventSpotter with your own Entity Repository

You can deploy EventSpotter with any repository or set of named entities, given that you have aritists and event descriptions(or their logical equivalents) for all entities. The database layout has to conform to the one described here. For a good example instance of all the data please look at [https://github.com/amark-india/eventspotter/tree/master/data/eventspotterdb.sql] .

#### Database Table
    
The mandatory database table is:

* EventMedia:
	 eventId, eventTitle, publisher, date, location, category , agent, eventDescription 

### Comparing Your NED Algorithm against EventSpotter

You can also compare your results on the datasets where we already ran EventSpotter, see below.

### Available Datasets

There are two main datasets we created to do research on EventSpotter. Both are available on the [EventSpotter website][EventSpotter].
Infact, both used a collection of 60 event descriptions selected at random from EventMedia repository. The only difference between the datasets being the way in which they were annotated.
A candidate spot is annotated with `<EVENT>` and `</EVENT>`, this can be easily transformed into the famous CoNLL format.

* GS-manual: Contained 115 valid event annotations. The manual annotation was broken down into two passes. In the first pass, an unbiased, rule based manual annotation was performed. In the second pass, the human annotators were given the list of event titles that were being described in the documents. With this posteriori knowledge the human annotators were able to annotate event occurrences which would have otherwise gone unannotated.
* GS-synthetic: Contained 122 valid event annotations. We carried out a straightforward string comparison between the input text and all event titles in the EventMedia dataset. If there was a match, the spot was annotated as an event.

We provide our readers these two datasets in the Golden dataset folder at [https://github.com/amark-india/eventspotter/tree/master/data/GoldenDataset]

## Further Information

If you are using EventSpotter, any parts of it or any datasets we made available, please give us credit by referencing EventSpotter in your work. If you are publishing scientific work based on EventSpotter, please cite us using the citation at the end of this document.

* Our EventSpotter project website: [eventspotter.eurecom.fr](http://eventspotter.eurecom.fr) (still working on getting the website up. You could send in your queries by mail.)

## Developers

The EventSpotter developers are (in alphabetical order):

* Amar KADAMALAKUNTE (Amar.Kadamalakunte@eurecom.fr)
* Giuseppe RIZZO (Giuseppe.Rizzo@eurecom.fr)
* Meghana SREEKANTA MURTHY (Meghana.Sreekanta-Murthy@eurecom.fr)
* Raphael TRONCY (Raphael.Troncy@eurecom.fr)



    
## Citing EventSpotter

If you use EventSpotter in your research, please cite EventSpotter:

    @inproceedings{EventSpotter2011,
      author = {Amar Kadamalakunte, Meghana Sreekanta Murthy, Giuseppe Rizzo, Raphael Troncy},
      title = {{EventSpotter- spotting entities that talk about events}},
      booktitle = {yet to be presented},
      year = {2013},
    }

## References
* H. Khrouf, V. Milicic, and R. Troncy, “EventMedia live: Exploring
events connections in real-time to enhance content,” in ISWC 2012, Se-
mantic Web Challenge at 11th International Semantic Web Conference,
November 11-15, 2012, Boston, USA, (Boston, UNITED STATES), 11
2012.
* D. Gruhl, M. Nagarajan, J. Pieper, and C. Robson, “Context and domain
knowledge enhanced entity spotting in informal text.”
* J. Hassell, B. Aleman-meza, and I. B. Arpinar, “Ontology-driven auto-
matic entity disambiguation in unstructured text,” in In International
Semantic Web Conference, pp. 44–57, 2006.
 ̃
* P. N. Mendes, M. Jakob, A. GarcAa-silva,
and C. Bizer, “Dbpedia spot-
light: Shedding light on the web of documents,” in In Proceedings of the
7th International Conference on Semantic Systems (I-Semantics, 2011.
* A. Ritter, S. Clark, and O. Etzioni, “Open domain event extraction from
twitter.”
* T. Steiner, S. van Hooland, and E. Summers, “Mj no more: Using con-
current wikipedia edit spikes with social network plausibility checks for
breaking news detection,” CoRR, vol. abs/1303.4702, 2013.

[EventSpotter]: https://github.com/amark-india/eventspotter
[Institut Eurecom]: https://www.eurecom.fr/en
[EventMedia_2012preprocessed]: https://github.com/amark-india/eventspotter/tree/master/data/EventMedia_2012preprocessed.csv 
[github repository]: https://github.com/amark-india/eventspotter

