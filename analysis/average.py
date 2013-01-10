#! /usr/bin/env python

import argparse, numpy, glob

def main(args):
    total = None
    files = glob.glob(args.pattern)
    nfiles = len(files)
    print "Averaging %d files" % nfiles
    for f in files:
        print f
        data = numpy.loadtxt(f, usecols=[args.valuecol])
        if total is None:
            total = data
        else:
            total += data
    
    print "Writing to output"
    with open(args.output, 'w') as f:
        for i,v in enumerate(total):
            print >>f, "%d\t%g" % (i,v/len(files))
    
def opts():
    parser = argparse.ArgumentParser()
    parser.add_argument('pattern', help='Filename pattern')
    parser.add_argument('output', help='Output file with average')
    parser.add_argument('--valuecol', type=int, default=1, help='Column with values to average')
    return parser

if __name__ == '__main__':
    main(opts().parse_args())