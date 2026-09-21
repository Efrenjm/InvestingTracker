#!/usr/bin/env bash
# Print one location code. No files, registry or persistent state are used.
set -euo pipefail
export LC_ALL=C

if (( $# != 0 )); then
  printf 'Usage: %s\n' "$0" >&2
  exit 2
fi

alphabet='abcdefghijklmnopqrstuvwxyz0123456789'
code=''
while (( ${#code} < 9 )); do
  bytes=$(od -An -N32 -tu1 /dev/urandom)
  for byte in $bytes; do
    # Reject the last four byte values to avoid modulo bias (252 = 36 * 7).
    if (( byte < 252 )); then
      index=$(( byte % 36 ))
      code+="${alphabet:index:1}"
      if (( ${#code} == 9 )); then break; fi
    fi
  done
done
printf '%s\n' "$code"
