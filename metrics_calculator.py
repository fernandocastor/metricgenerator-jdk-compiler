#!/usr/bin/env python
from __future__ import division
import numpy
import re

def package_name(full_classname):
    match = re.search(r'^([a-z]+\.)*', full_classname)
    if match is None:
        return None
    else:
        if len(match.group(0)) > 0:
            return match.group(0)[:-1]
        else:
            return '<main_package>'

if __name__ == '__main__':
    entries = open('all_metrics_output.txt', 'r')
    metrics = {}
    entities = {'class': set(), 'method': set(), 'packages': {}}

    for entry in entries:
        words = [word.strip() for word in entry.split(';')]
        if words[0] == 'declaration':
            [type_, name] = words[1:]
            if type_[-5:] == 'class':
                entities['class'].add(name)
                try:
                    entities['packages'][package_name(name)].add(name)
                except KeyError:
                    entities['packages'][package_name(name)] = set([name])
            elif type_ == 'method':
                entities['method'].add(name)
        else:
            [type_, name, concern] = words
            if metrics.get(concern) is None:
                metrics[concern] = {'class': set(), 'method': set(), 'package': set()}
            if type_ == 'class':
                metrics[concern]['class'].add(name)
                metrics[concern]['package'].add(package_name(name))
            elif type_ == 'method':
                metrics[concern]['method'].add(name)

    entries.close()
    metrics_output_file = open('metrics.txt', 'w')
    tc = len(entities['class'])
    tm = len(entities['method'])
    tp = len(entities['packages'])
    all_cdc = []
    all_cdo = []
    all_cdp = []
    all_cdctc = []
    all_cdotm = []
    all_cdptp = []

    for (concern, metrics) in metrics.iteritems():
        cdc = len(metrics['class'])
        cdo = len(metrics['method'])
        cdp = len(metrics['package'])
        cdctc = round(cdc/tc, 5)
        cdotm = round(cdo/tm, 5)
        cdptp = round(cdp/tp, 5)
        all_cdc.append(cdc)
        all_cdo.append(cdo)
        all_cdp.append(cdp)
        all_cdctc.append(cdctc)
        all_cdotm.append(cdotm)
        all_cdptp.append(cdptp)
        metrics_output_file.write(
            '%s\n'
            '    CDC %d\n'
            '    CDO %d\n'
            '    CDP %d\n'
            '    CDC/TC %.5f\n'
            '    CDO/TO %.5f\n'
            '    CDP/TP %.5f\n' %
                (concern, cdc, cdo, cdp, round(cdc/tc, 5), round(cdo/tm, 5), round(cdp/tp, 5))
        )
        packages_and_local_cdc = filter(
            lambda (package, local_cdc): local_cdc > 0,
            sorted([
                (package, len(metrics['class'] & classes_of_package))
                    for (package, classes_of_package)
                    in entities['packages'].iteritems()
            ], key=lambda (package, local_cdc): local_cdc)
        )
        for (package, local_cdc) in packages_and_local_cdc:
            metrics_output_file.write('    CDC %d for package "%s"\n'
                                            % (local_cdc, package))
        metrics_output_file.write('\n')

    metrics_output_file.write(
        'MEAN\n'
        '    CDC %.2f\n'
        '    CDO %.2f\n'
        '    CDP %.2f\n'
        '    CDC/TC %.5f\n'
        '    CDO/TO %.5f\n'
        '    CDP/TP %.5f\n\n'
            % (numpy.mean(all_cdc), numpy.mean(all_cdo), numpy.mean(all_cdp),
               numpy.mean(all_cdctc), numpy.mean(all_cdotm), numpy.mean(all_cdptp))
    )
    metrics_output_file.write(
        'STD\n'
        '    CDC %.2f\n'
        '    CDO %.2f\n'
        '    CDP %.2f\n'
        '    CDC/TC %.5f\n'
        '    CDO/TO %.5f\n'
        '    CDP/TP %.5f\n\n'
            % (numpy.std(all_cdc), numpy.std(all_cdo), numpy.std(all_cdp),
               numpy.std(all_cdctc), numpy.std(all_cdotm), numpy.std(all_cdptp))
    )
