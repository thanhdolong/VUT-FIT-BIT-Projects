#!/usr/bin/php
<?php
/**
 * User: xdolon00
 * 9/10
 */

mb_internal_encoding('UTF-8');
mb_regex_encoding('UTF-8');

function arguments(&$determination, &$caseInSensitive, &$noEps, &$help, &$inputAdr, &$inputFile, &$outputAdr, &$outputFile, &$argc, &$argv)
{

    for ($i = 1; $i < $argc; $i++) {

        if (($i > 1) && ($help == true)) exit(1);

        if ($argv[$i] == '-h' || $argv[$i] == '--help') {
            // Calling help
            if ($i == 1) $help = true;
            else {
                fwrite(STDERR, "Error - Calling help\n");
                exit(1);
            }
        } else if (($argv[$i] == '-i') || ($argv[$i] == '--case-insensitive')) {
            // Calling case in sensitive
            if ($caseInSensitive == true) {
                fwrite(STDERR, "Error - Calling case in sensitive\n");
                exit(1);
            }
            $caseInSensitive = true;
        } else if (($argv[$i] == '-d') || ($argv[$i] == '--determinization')) {
            // calling $determination
            if ($determination == true || $noEps == true) {
                fwrite(STDERR, "Error - Calling determination or no epsilon rules\n");
                exit(1);
            }
            $determination = true;
        } else if (($argv[$i] == '-e') || ($argv[$i] == '--no-epsilon-rules')) {
            // calling no epsilon rules
            if ($noEps == true || $determination == true) {
                fwrite(STDERR, "Error - Calling determination or no epsilon rules\n");
                exit(1);
            }
            $noEps = true;
        } else if (substr($argv[$i], 0, 7) == "--input") {
            // Calling input
            if ($inputAdr == true) {
                fwrite(STDERR, "Error - Calling input\n");
                exit(2);
            }
            $inputAdr = true;
            $inputFile = substr($argv[$i], 8);
            if (!file_exists($inputFile)) {
                fwrite(STDERR, "Error - The file $inputFile does not exist\n");
                exit(2);
            } else if (!is_readable($inputFile)) {
                fwrite(STDERR, "Error - can't read\n");
                exit(2);
            }
        } else if (substr($argv[$i], 0, 8) == "--output") {
            // Calling output
            if ($outputAdr == true) {
                fwrite(STDERR, "Error - Calling output\n");
                exit(3);
            }
            $outputAdr = true;
            $outputFile = substr($argv[$i], 9);

            if (file_exists($outputFile)) {
                if (!is_writable($outputFile)) {
                    fwrite(STDERR, "Error - can't write\n");
                    exit(3);
                }
            }

        } else {
            fwrite(STDERR, "Error - wrong parameters\n");
            exit(1);
        }

    }
}


function createAutomaton(&$inputFile, &$automaton, &$caseInSensitive){
    $input = file($inputFile);

    $x = 0;
    foreach ($input as &$line) {
        // lower case
        if ($caseInSensitive == true) $line = mb_strtolower($line, 'UTF-8');

        // delete comments
        $line = mb_ereg_replace('#(?=[^\'])(?<=[^\'])', "#@~#\n", $line);
        $line = array_filter(explode('#@~#', $line));
        for ($i = count($line); $i > 0; $i--) {
            unset($line[$i]);
        }
        $input[$x]  = implode($line);
        $x++;
    }
    $string = implode($input);
    $string = trim(mb_ereg_replace('/,\s+,/', ' ', $string));


    // Validation

    $syntaxCheck = '^[\s]*\([\s]*\{[\s\S]*\}[\s]*,[\s]*\{[\s\S]*}[\s]*,[\s]*{[\s\S]*}[s]*,[\s\S]*,[\s]*{[\s\S]*}[\s]*\)[\s]*$';
    if ((mb_ereg($syntaxCheck, $string))==false) {
        fwrite(STDERR, "automaton syntax error.\n");
        exit(40);
    }

    $string = mb_ereg_replace('\{\}', "{_ERROR_}", $string);

    #-------------------------------- PARSING -------------------------------#
    $string = mb_ereg_replace('\{\}', "{_ERROR_}", $string);
    $string = mb_ereg_replace('\s*\(\s*{', "", $string);
    $string = mb_ereg_replace('\s*\}\s*\,\s*\{\s*', "#@@$@@#", $string);
    $string = mb_ereg_replace('\s*\}\s*\,\s*', "#@@$@@#", $string);
    $string = mb_ereg_replace('\s*\,\s*\{\s*', "#@@$@@#", $string);
    $string = mb_ereg_replace('\s*\}\s*\)\s*', "", $string);
    $arrayOfStrings = explode("#@@$@@#", $string);


    #---------------------------------- STAVY ---------------------------------#
    $arrayOfStrings[0] = mb_ereg_replace('\s*\,\s*', ",", $arrayOfStrings[0]);
    $arrayOfStrings[0] = mb_ereg_replace('\s*', "", $arrayOfStrings[0]);
    $automaton["states"] = explode(",", $arrayOfStrings[0]);

    // bez hacku a carek - ^[a-zA-Z]([_0-9a-zA-Z]*[0-9a-zA-Z])?$
    for ($i = 0; $i < count($automaton["states"]); $i++) {
        if (($automaton["states"][$i] == "_ERROR_") && (count($automaton["states"]) == 1)){
            fwrite(STDERR, "Empty states\n");
            exit(41);
        }

        if (!(mb_ereg_match('^[a-zA-Z]([_0-9a-zA-Z]*[0-9a-zA-Z])?$', $automaton["states"][$i]))) {
            fwrite(STDERR, "Wrong identifier name\n");
            exit(40);
        }
    }

    sort($automaton["states"], SORT_STRING);
    $automaton["states"] = array_unique($automaton["states"]);
    $automaton["states"] = array_values($automaton["states"]);

    #---------------------------------- Abeceda ---------------------------------#

    $arrayOfStrings[1] = mb_ereg_replace('[\s]*(?<=\'.\'|\'\'\'\')[\s]*,', '#@@##', $arrayOfStrings[1]);
    $arrayOfStrings[1] = mb_ereg_replace('\#@@##\'(?!.\')\s*', '', $arrayOfStrings[1]);
    $automaton["alphabet"] = mb_split('#@@##', $arrayOfStrings[1]);

    sort($automaton["alphabet"], SORT_STRING);
    $automaton["alphabet"] = array_unique($automaton["alphabet"]);
    $automaton["alphabet"] = array_values($automaton["alphabet"]);


    for ($i = 0; $i < count($automaton["alphabet"]); $i++) {

        $automaton["alphabet"][$i] = mb_ereg_replace('(\s*(?=\'.\')|(?<=\'.\')\s*)', "", $automaton["alphabet"][$i]);
        if ($automaton["alphabet"][0] == "_ERROR_") {
            fwrite(STDERR, "Empty alphabet\n");
            exit(41);
        }

        if (!(mb_ereg_match('^(\'[^\']\'|\'\'\'\')$', $automaton["alphabet"][$i]))) {
            fwrite(STDERR, "Wrong alphabet\n");
            exit(40);
        }

    }

    #--------------------------------- PRAVIDLA --------------------------------#


    if ((mb_ereg('-(\s)+(?=\>)', $arrayOfStrings[2]))) {
        fwrite(STDERR, "Wrong rules -> \n");
        exit(40);
    }

    $arrayOfStrings[2] = mb_ereg_replace('(\s*(?!\')|(?<!\')\s*)', "", $arrayOfStrings[2]);
    $automaton["rules"] = mb_split('\s*,\s*(?=([[a-zA-Z]]([[0-9a-zA-Z]_]*[[0-9a-zA-Z]])?)\s*\'([^\']|\'\')?\'\s*->\s*([^[:digit:]_]([[:alnum:]_]*[[:alpha:]])?))',  $arrayOfStrings[2]);


    sort($automaton["rules"], SORT_STRING);
    $automaton["rules"] = array_unique($automaton["rules"]);
    $automaton["rules"] = array_values($automaton["rules"]);


    for ($i = 0; $i < count($automaton["rules"]); $i++) {

        if (!(mb_ereg_match('^((\s*.*\s*(\'[^\']\'|\'\'\'\'|\'\')\s*\-(?=>)\s*.*\s*)|_ERROR_)$', $automaton["rules"][$i]))) {
            fwrite(STDERR, "Wrong rules\n");
            exit(40);
        }

        $automaton["rules"][$i] = mb_ereg_replace('(?<!\')\s(?!\')', "", $automaton["rules"][$i]);
        $automaton["rules"][$i] = mb_ereg_replace('\-\>', "#@@##", $automaton["rules"][$i]);
        $automaton["rules"][$i] = mb_ereg_replace('\'(?=([^\']\'|\'\'\'|((?<=[^\']\')\'#@@##)))', "#@@##'", $automaton["rules"][$i]);
        $automaton["rules"][$i] = mb_ereg_replace('\s(?=#@@##)', "", $automaton["rules"][$i]);
        $automaton["rules"][$i] = explode("#@@##", $automaton["rules"][$i]);


    }

    #---------------------------------- Pocatecny stav --------------------------------#
    $automaton["begin"] = $arrayOfStrings[3];

    for ($i = 0; $i < count($automaton["begin"]); $i++) {
        if (!(mb_ereg_match('^[a-žA-Ž]([_0-9a-žA-Ž]*[0-9a-žA-Ž])?$', $automaton["states"][$i]))) {
            fwrite(STDERR, "Wrong Beginning state\n");
            exit(40);
        }
    }


    #----------------------------------- Koncovy stav ---------------------------------#

    $arrayOfStrings[4] = mb_ereg_replace('\s*\,\s*', ",", $arrayOfStrings[4]);
    //$arrayOfStrings[4] = mb_ereg_replace('\s*', "", $arrayOfStrings[4]);
    $automaton["end"] = explode(",", $arrayOfStrings[4]);

    // regex pro povoleni hacek a carek: (/^[^\\W\\d_]([^\\W]*[^\\W_]))?$/u
    for ($i = 0; $i < count($automaton["end"]); $i++) {
        for ($z = 0; $z < count($automaton["states"]); $z++) {
            if (!(mb_ereg_match('^[a-zA-Z]([_0-9a-zA-Z]*[0-9a-zA-Z])?$', $automaton["states"][$z]))) {
                fwrite(STDERR, "Wrong Ending state\n");
                exit(40);
            }
        }
    }

    sort($automaton["end"], SORT_STRING);
    $automaton["end"] = array_unique($automaton["end"]);
    $automaton["end"] = array_values($automaton["end"]);
}


function semanticValidator(&$automaton){

    global $validation;
    $validation = false;


    #--------------------------------- PRAVIDLA --------------------------------#
    if (($automaton["rules"][0][0] == "_ERROR_") && (count($automaton["rules"]) == 1)){
        $automaton["rules"][0] = str_replace("_ERROR_","", $automaton["rules"]);
    }
    else {
        for ($i = 0; $i < count($automaton["rules"]); $i++) {

            for ($xState = 0; $xState < count($automaton["states"]); $xState++) {

                if ($automaton["rules"][$i][0] === $automaton["states"][$xState]) {
                    $validation = true;
                    break;
                }

            }

            if ($validation == true) {
                $validation = false;
            } else {
                fwrite(STDERR, "Error: Missing state in rules(1)\n");
                exit(41);
            }
        }

        for ($i = 0; $i < count($automaton["rules"]); $i++) {
            for ($xState = 0; $xState < count($automaton["alphabet"]); $xState++) {
                if (empty($automaton["rules"][$i])) {
                    $validation = true;
                    break;
                }

                if ($automaton["rules"][$i][1] === $automaton["alphabet"][$xState]) {
                    $validation = true;
                    break;
                } else if ($automaton["rules"][$i][1] == "''") {
                    $validation = true;
                    break;
                }
            }

            if ($validation == true) {
                $validation = false;
            } else {
                fwrite(STDERR, "Error: Missing state in rules(2)\n");
                exit(41);
            }
        }

        for ($i = 0; $i < count($automaton["rules"]); $i++) {
            for ($xState = 0; $xState < count($automaton["states"]); $xState++) {
                if (empty($automaton["rules"][$i])) {
                    $validation = true;
                    break;
                }

                if ($automaton["rules"][$i][2] === $automaton["states"][$xState]) {
                    $validation = true;
                    break;
                }
            }

            if ($validation == true) {
                $validation = false;
            } else {
                fwrite(STDERR, "Error: Missing state in rules(3)\n");
                exit(41);
            }
        }
    }
    #------------------------------ POCATECNI STAV -----------------------------#

    for ($xState = 0; $xState < count($automaton["states"]); $xState++) {
        if ($automaton["begin"] == $automaton["states"][$xState]) {
            $validation = true;
            break;
        }
        if (($automaton["begin"] == "") && (count($automaton["begin"]) == 1)){
            fwrite(STDERR, "Error: Missing beginning state\n");
            exit(41);
        }
    }

    if ($validation == true){
        $validation = false;
    } else {
        fwrite(STDERR, "Error: Missing beginning state\n");
        exit(40);
    }

    #--------------------------------- KONCOVY STAV --------------------------------#

    for ($i = 0; $i < count($automaton["end"]); $i++) {
        for ($xState = 0; $xState < count($automaton["states"]); $xState++){
            if ($automaton["end"][$i] == $automaton["states"][$xState]) {
                $validation = true;
                break;
            } else if (($automaton["end"][$i] == "_ERROR_") && (count($automaton["end"]) == 1)){
                $automaton["end"][$i] = mb_ereg_replace("_ERROR_","", $automaton["end"][$i]);
                $validation = true;
                break;
            }
        }

        if ($validation == true){
            $validation = false;
        } else {
            fwrite(STDERR, "Error: Validating of Ending state .\n");
            exit(41);
        }
    }
}


function printAutomaton(&$automaton, &$output) {

    array_multisort($automaton["states"]);
    array_multisort($automaton["alphabet"]);
    if (count($automaton["rules"]) != 0) array_multisort($automaton["rules"]);
    if (count($automaton["end"]) != 0) array_multisort($automaton["end"]);


    $automaton["states"] = array_unique($automaton["states"]);
    $automaton["states"] = array_values($automaton["states"]);

    $automaton["alphabet"] = array_unique($automaton["alphabet"]);
    $automaton["alphabet"] = array_values($automaton["alphabet"]);

    $automaton["end"] = array_unique($automaton["end"]);
    $automaton["end"] = array_values($automaton["end"]);


    $output = "(\n";

    #---------------------------------- STAVY ---------------------------------#
    $output .= "{";
    $output .= implode(', ', $automaton["states"]);
    $output .= "},\n";

    #---------------------------------- Abeceda ---------------------------------#
    $output .= "{";
    $output .= implode(', ', $automaton["alphabet"]);
    $output .= "},\n";

    #--------------------------------- PRAVIDLA --------------------------------#
    $output .= "{\n";
    $x = 1;
    if (count($automaton["rules"]) != 0) {
        for ($i = 0; $i < count($automaton["rules"]); $i++) {
            $output .= $automaton["rules"][$i][0] . ' ' . $automaton["rules"][$i][1] . " -> " . $automaton["rules"][$i][2];
            if ($x == count($automaton["rules"])) $output .= "\n";
            else $output .= ",\n";
            $x++;
        }
    }
    $output .= "},\n";

    #---------------------------------- Pocatecny stav --------------------------------#
    $output .= $automaton["begin"];
    $output .= ",\n";

    #---------------------------------- Abeceda ---------------------------------#
    $output .= "{";
    $output .= implode(', ', $automaton["end"]);
    $output .= "}";

    $output .= "\n)\n";
}


function noEpsilon(&$automaton)
{


    static $beginautomaton;
    static $R;

    foreach ($automaton["states"] as $statesstring){
        $Q[0] = array($statesstring);
        $i = 0;
        do {
            $i++;
            array_push($Q, $Q[$i - 1]);
            foreach ($Q[$i] as $Qstring) {
                for ($z = 0; $z < count($automaton["rules"]); $z++) {
                    if (($Qstring == $automaton["rules"][$z][0]) && ($automaton["rules"][$z][1] == "''")) {
                        array_push($Q[$i], $automaton["rules"][$z][2]);
                    }
                }
            }

            array_multisort($Q[$i]);

            $Q[$i] = array_unique($Q[$i]);
            $Q[$i] = array_values($Q[$i]);

        } while ((bool)(array_diff($Q[$i], $Q[$i - 1])));

        $Q = array_pop($Q);

        foreach ($Q as $newQ) {
            // vytvareni novych pravidel
            for ($i = 0; $i < count($automaton["rules"]); $i++){
                if (($newQ == $automaton["rules"][$i][0]) && ($automaton["rules"][$i][1] != "''")) {
                    $R .= $statesstring."#@@##".$automaton["rules"][$i][1]."#@@##".$automaton["rules"][$i][2]."\n";
                }
            }
        }

        foreach ($Q as $newQ){
            for ($i = 0; $i < count($automaton["end"]); $i++) {
                if ($newQ == $automaton["end"][$i]) {
                    array_push($automaton["end"], $statesstring);
                    break;
                }
            }
        }


        array_splice($Q, 0);
    }

    array_splice($automaton["rules"], 0);

    $automaton["rules"] = mb_split('\n', $R);
    array_pop($automaton["rules"]);

    sort($automaton["rules"], SORT_STRING);
    $automaton["rules"] = array_unique($automaton["rules"]);
    $automaton["rules"] = array_values($automaton["rules"]);


    for ($i = 0; $i < count($automaton["rules"]); $i++) {
        $automaton["rules"][$i] = explode("#@@##", $automaton["rules"][$i]);
    }
}


function determination(&$automaton) {
    global $cilovyStav;
    global $pocatecnistav;
    global $Rd;


    $cilovyStav = "";
    $vstupnisymbol = "";
    $pocatecnistav = "";
    $pocatecnistav2 = "";

    $Qrules = array();
    $Qnew[] = array($automaton["begin"]);

    $Qd = array($automaton["begin"]);
    $fd = array();

    noEpsilon($automaton);
    do{
        $Qtmp = array_pop($Qnew);

        for ($i = 0; $i < count($automaton["alphabet"]); $i++) {
            for ($q = 0; $q < count($Qtmp); $q++) {
                for ($x = 0; $x < count($automaton["rules"]); $x++) {
                    if (($automaton["alphabet"][$i] == $automaton["rules"][$x][1]) && ($automaton["rules"][$x][0] == $Qtmp[$q])) {
                        array_push($Qrules, $automaton["rules"][$x][2]);
                        $vstupnisymbol = $x;
                    }
                }
            }

            if (count($Qrules) !=0 ){
                array_multisort($Qrules);
                $Qrules = array_unique($Qrules);
                $Qrules = array_values($Qrules);

                for ($z = 0; $z < count($Qrules); $z++) {
                    if ($z == 0) $cilovyStav .= $Qrules[$z];
                    else $cilovyStav .= "_" . $Qrules[$z];
                }

                array_multisort($Qtmp);
                $Qtmp = array_unique($Qtmp);
                $Qtmp = array_values($Qtmp);

                // Konkatenace pocatecniho stavu
                for ($z = 0; $z < count($Qtmp); $z++) {
                    if ($z == 0) $pocatecnistav .= $Qtmp[$z];
                    else $pocatecnistav .= "_" . $Qtmp[$z];
                }

                $pocatecnistav2 = $pocatecnistav;

                $Rd [] = array();
                array_push($Rd[count($Rd) - 1], $pocatecnistav,$automaton["rules"][$vstupnisymbol][1], $cilovyStav);
            }

            if (!in_array($cilovyStav,$Qd) && ($cilovyStav != "")) {
                array_push($Qnew, $Qrules);
                array_push($Qd, $cilovyStav);
            }


            $cilovyStav = "";
            $pocatecnistav = "";
            array_splice($Qrules, 0);
        }
        if ((bool) array_intersect($Qtmp, $automaton["end"]) == true){
            array_push($fd,$pocatecnistav2);
            $pocatecnistav2 = "";
        }
        array_splice($Qtmp, 0);

    } while (count($Qnew) != 0);


    unset($automaton["rules"]);
    $automaton["rules"] = $Rd;

    if (count($automaton["end"]) != 0) {
        unset($automaton["end"]);
        $automaton["end"] = $fd;
    }

    unset($automaton["states"]);
    $automaton["states"] = $Qd;
}

$help = false;
$determination = false;
$caseInSensitive = false;
$noEps = false;
$inputAdr = false;
$outputAdr = false;

$automaton = [ "states" => "", "alphabet" => "", "rules" => "", "begin" => "", "end" => ""];

$inputFile = "php://stdin";
$outputFile = "php://stdout";
$output = "";

arguments($determination, $caseInSensitive, $noEps, $help, $inputAdr, $inputFile, $outputAdr, $outputFile, $argc, $argv);

if ($help == true) {
    help();
    exit(0);
}

createAutomaton($inputFile, $automaton, $caseInSensitive);
semanticValidator($automaton);
if ($noEps == true) noEpsilon($automaton);

if ($determination == true) determination($automaton);
printAutomaton($automaton, $output);

if ($outputAdr == true){

    $data = mb_convert_encoding($output, 'UTF-8');
    file_put_contents($outputFile, $output);

} else echo $output;

/*
Test parameteres
echo "\n";

echo $determination ? "Determinizace: true\n" : "Determinizace: false\n";
echo $caseInSensitive ? "Case in sensitive: true\n" : "Case in sensitive: false\n";
echo $noEps ? "No Epsilon: true\n" : "No Epsilon: false\n";
echo $inputAdr ? "InputFile: true - Adress is: $inputFile \n" : "InputFile: false - Adress is: $inputFile \n";
echo $outputAdr ? "OutputFile: true - Adress is: $outputFile\n " : "OutputFile: false - Adress is: $outputFile \n";
*/

// Calling Help

function help(){
    echo("\t--input=filename\n\t\t\tvstupny textovy soubor na adrese filename.\n\n");
    echo("\t--output=filename\n\t\t\tvystupny textovy soubor na adrese filename.\n\n");
    echo("\t-e, --no-epsilon-rules\n\t\t\todstraneni epsilon prechodu vstupniho automatu.\n\n");
    echo("\t-d, --determinization\n\t\t\tvykona determinizace vstupniho automatu bez generovani nedostupnych stavuv.\n\n");
    echo("\tPozn.: Pokial nie je zadany ani jeden z prepinacov -d a -e, je vstupny automat validovany a prevedeny na normalny vystup\n\n");
    echo("\t-i, --case-insensitive\n\t\t\tnebere ohled na velikost znaku, na vystupe jsou velke pismena prevedene na male\n\n");
}

exit(0);
?>
