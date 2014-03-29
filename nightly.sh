#!/bin/bash -ev

# -e makes the whole thing die with an error if any command does
# -v lets you see the commands as they happen

if [ ! -f "models/.git" ] ; then
  git submodule update --init
fi

if [ "$1" == --clean ] ; then
  git clean -fdX
  git submodule foreach git clean -fdX
fi

rm -rf tmp/nightly
mkdir -p tmp/nightly

# here we're using pipes so "-e" isn't enough to stop when something fails.
# maybe there's an easier way, than I've done it below, I don't know.
# I suck at shell scripting - ST 2/15/11

./sbt test:compile "ensime generate" 2>&1 | tee tmp/nightly/compile.txt
if [ ${PIPESTATUS[0]} -ne 0 ] ; then echo "*** FAILED: test:compile"; exit 1; fi
echo "*** done: test:compile"

./sbt 'fast:test' 2>&1 | tee tmp/nightly/fast-test.txt
if [ ${PIPESTATUS[0]} -ne 0 ] ; then echo "*** FAILED: fast:test"; exit 1; fi
echo "*** done: fast:test"

./sbt 'test' 2>&1 | tee tmp/nightly/test.txt
if [ ${PIPESTATUS[0]} -ne 0 ] ; then echo "*** FAILED: test"; exit 1; fi
echo "*** done: test"

echo "****** all done!"
